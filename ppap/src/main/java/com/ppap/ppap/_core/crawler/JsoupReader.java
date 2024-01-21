package com.ppap.ppap._core.crawler;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap.domain.subscribe.entity.Notice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsoupReader {

	public List<CrawlingData> getMechanicNoticeData(String url, LocalDateTime maxPubDateTime, boolean isInit) throws IOException {
		Connection connection = Jsoup.connect(url);
		Document document = connection.get();
		Elements element = document.getElementsByClass("board-list02");
		Elements subElements = element.first().select("tbody>tr");
		List<CrawlingData> crawalingDatas = subElements.stream()
			.filter(subElement  -> !subElement.attr("class").equals("notice"))
			.map(subElement -> {
				String id = subElement.getElementsByClass("number").first().text();
				String title = subElement.getElementsByClass("title left").first().child(0).ownText();
				String category = subElement.getElementsByClass("type").isEmpty() ? null : subElement.getElementsByClass("type").first().text();
				String href = subElement.getElementsByClass("title left").first().child(0).attr("href");
				String contentId = href.substring(20,href.length()-1);
				String author = subElement.getElementsByClass("writer").text();
				LocalDateTime pubDate = getLocalDateTime(contentId, subElement.getElementsByClass("date").text());
				//아래 코드 수정필요 sub01_()..로 될 수 있게
				String detailUrl = String.format("%s?seq=%s&db=hakbunotice&page=1&perPage=20&SearchPart=BD_SUBJECT&SearchStr=&page_mode=view", url, contentId);
				if (title.endsWith("...") && (pubDate.isAfter(maxPubDateTime) || isInit)) {
					// detailUrl을 통해서 데이터 더 가져오기
					Connection detailUrlConnection = Jsoup.connect(detailUrl);
					try {
						Document detailUrlDocument = detailUrlConnection.get();
						Element detailUrlElement = detailUrlDocument.getElementsByClass("board-view").first().child(0);
						title = detailUrlElement.select("dd").text();
					} catch (SocketTimeoutException e) {
						log.error(ExceptionUtils.getMessage(e));
						throw new Exception500(BaseExceptionStatus.JSOUP_LINK_NETWORK_ERROR);
					} catch (IOException e) {
						log.error(ExceptionUtils.getMessage(e));
						throw new Exception500(BaseExceptionStatus.JSOUP_LINK_UNKNOWN_ERROR);
					}
				}
				return (CrawlingData) JsoupData.builder()
					.title(title)
					.pubDate(pubDate)
					.link(detailUrl)
					.author(author)
					.category(category)
					.build();})
			.toList();
		return crawalingDatas;
	}

	private LocalDateTime getLocalDateTime(String id, String time) {
		int idInt =  Integer.parseInt(id);

		int hour = idInt/1000/3600;
		hour = hour > 23 ? hour%24 : hour;
		int minute = idInt/1000%3600/60;
		int second = idInt/1000%3600%60;
		int miliSecond = idInt%1000*1_000_000;
		return LocalDate.parse(time).atTime(hour, minute, second,miliSecond);
	}
}
