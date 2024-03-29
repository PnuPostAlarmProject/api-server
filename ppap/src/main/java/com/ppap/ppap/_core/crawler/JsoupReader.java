package com.ppap.ppap._core.crawler;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap._core.utils.JsoupFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsoupReader {
	private final JsoupFactory jsoupFactory;

	public List<CrawlingData> getMechanicNoticeData(String url, LocalDateTime maxPubDateTime, boolean isInit)
		throws IOException {
		Document document = jsoupFactory.getDocumentData(url);
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
				String detailUrl = String.format("%s?seq=%s&db=%s&page=1&perPage=20&SearchPart=BD_SUBJECT&SearchStr=&page_mode=view",
					url,
					contentId,
					getNoticeDbType(url));
				// 제목이 길어 ...로 끝난다면 추가적으로 요청해 데이터를 가져온다.
				if (title.endsWith("...") && (pubDate.isAfter(maxPubDateTime) || isInit)) {
					try {
						Document detailUrlDocument = jsoupFactory.getDocumentData(detailUrl);
						Element detailUrlElement = detailUrlDocument.getElementsByClass("board-view").first().child(0);
						title = detailUrlElement.select("dd").text();
					} catch (SocketTimeoutException e) {
						log.error(ExceptionUtils.getStackTrace(e));
						throw new Exception500(BaseExceptionStatus.JSOUP_LINK_NETWORK_ERROR);
					} catch (IOException e) {
						log.error(ExceptionUtils.getStackTrace(e));
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

		// 크롤링한 데이터에 null이 있는 경우가 존재해 예외처리
		return filterCrawlingDatas(crawalingDatas, maxPubDateTime, isInit);
	}

	public List<CrawlingData> getHakjisiNoticeData(String hakjisiUrl, LocalDateTime maxPubDateTime, boolean isInit)
		throws IOException {
		Document document = jsoupFactory.getDocumentData(hakjisiUrl);
		Elements elements = document.getElementsByClass("board-item");

		List<CrawlingData> crawalingDatas = elements.stream()
			.filter(boardItem -> boardItem.select("a > div.item-title > span.noti").text().isEmpty())
			.map(filteredBoarderItem -> {
				// System.out.println(filteredBoarderItem.select("a > div.item-title > span.board-title"));
				String title = filteredBoarderItem.select("a > div.item-title > span.board-title").text();
				String href = filteredBoarderItem.select("a").attr("href");
				String[] splitHref = href.split(",");
				String menuCD = splitHref[0].split("'")[1];
				String noticeId = splitHref[1].split("'")[1];
				String noticeLink = String.format("https://onestop.pusan.ac.kr/page?menuCD=%s&mode=DETAIL&seq=%s",
					menuCD,
					noticeId);
				String author = filteredBoarderItem.select("a > div.item-writer > span").text();
				String time = filteredBoarderItem.select("a > time.item-date").text();
				return (CrawlingData) JsoupData.builder()
					.link(noticeLink)
					.author(author)
					.title(title)
					.category(null)
					.pubDate(getLocalDateTime(noticeId, time))
					.build();
			})
			.toList();

		return filterCrawlingDatas(crawalingDatas, maxPubDateTime, isInit);
	}


	public List<CrawlingData> getUnivNoticeData(String univUrl, LocalDateTime maxPubDateTime, boolean isInit) throws IOException {
		Document document = jsoupFactory.getDocumentData(univUrl);
		Elements elements = document.getElementsByClass("board-list-table");
		Elements bodies = elements.first().select("tbody > tr");

		List<CrawlingData> crawlingDatas = bodies.stream()
			.filter(data -> data.className().isEmpty())
			.map(filteredData -> {
				String href = filteredData.select("td.subject > p.stitle > a").attr("href");
				String noticeId = href.split("=")[4];
				String link = String.format("https://www.pusan.ac.kr/kor/CMS/Board/PopupBoard.do?mgr_seq=3&mode=view&mgr_seq=3&board_seq=%s", noticeId);
				String title = filteredData.select("td.subject > p.stitle > a").text();
				String author = filteredData.select("td.writer").text();
				String date = filteredData.select("td.date").text();
				LocalDateTime pubdate = getLocalDateTime(noticeId, date);

				if (title.endsWith("...") && (pubdate.isAfter(maxPubDateTime) || isInit)) {
					try {
						Document detailDocument = jsoupFactory.getDocumentData(link);
						title = detailDocument.getElementsByClass("vtitle").text();
					} catch (IOException e) {
						throw new Exception500(BaseExceptionStatus.JSOUP_LINK_NETWORK_ERROR);
					}
				}

				return (CrawlingData) JsoupData.builder()
					.title(title)
					.link(link)
					.category(null)
					.author(author)
					.pubDate(pubdate)
					.build();
			})
			.toList();

		return filterCrawlingDatas(crawlingDatas, maxPubDateTime, isInit);
	}


	private List<CrawlingData> filterCrawlingDatas(List<CrawlingData> crawalingDatas,
		LocalDateTime maxPubDateTime, boolean isInit) {

		crawalingDatas = crawalingDatas.stream()
			.filter(data -> {
				if (Objects.isNull(data.pubDate()))
					return false;
				return !Objects.isNull(data.link()) && !data.link().isBlank();
			})
			.toList();
		return isInit ? crawalingDatas : crawalingDatas.stream()
			.filter(data -> data.pubDate().isAfter(maxPubDateTime))
			.toList();

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

	private String getNoticeDbType(String url) {
		for (MechanicNoticeEnum type : MechanicNoticeEnum.values()) {
			if (url.endsWith(type.getSuffix()))
				return type.getDbType();
		}
		throw new Exception500(BaseExceptionStatus.JSOUP_LINK_NETWORK_ERROR);
	}
}
