package com.ppap.ppap._core.scheduler;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ppap.ppap._core.crawler.CrawlingData;
import com.ppap.ppap._core.crawler.JsoupReader;
import com.ppap.ppap._core.firebase.message.FcmService;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.entity.constant.NoticeType;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import com.ppap.ppap.domain.subscribe.service.ContentWriteService;
import com.ppap.ppap.domain.subscribe.service.NoticeReadService;
import com.ppap.ppap.domain.subscribe.service.NoticeWriteService;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.service.DeviceReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 기계공학부를 위한 Jsoup 크롤링 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JsoupSchedulerService {
	private final NoticeReadService noticeReadService;
	private final NoticeWriteService noticeWriteService;
	private final ContentReadService contentReadService;
	private final ContentWriteService contentWriteService;
	private final SubscribeReadService subscribeReadService;
	private final DeviceReadService deviceReadService;
	private final FcmService fcmService;
	private final JsoupReader jsoupReader;

	@Scheduled(cron = "0 0/10 * * * *", zone = "Asia/Seoul")
	public void run() {
		MDC.put("logFileName","schedule");
		MDC.put("kind", "Jsoup");
		log.info("Jsoup cron start");
		long start = System.currentTimeMillis();
		try{

			processJsoupData();

		} catch (IOException ignore) {
		} finally {
			log.info("실행시간 : {} ms", System.currentTimeMillis()-start);
			log.info("Jsoup cron end");
			MDC.clear();
		}
	}

	private void processJsoupData() throws IOException {
		List<Notice> noticeList = noticeReadService.findByNoticeType(NoticeType.JSOUP);
		// System.out.println(jsoupReader.getMechanicNoticeData(noticeList.get(0).getLink()));
		Set<Long> noticeIdSetInContent = contentReadService.findDistinctNoticeIdIn(noticeList);
		Map<Notice, String> errorNotices = new HashMap<>();
		Map<Notice, List<CrawlingData>> filterNoticeJsoupGroup = getFilterNoticeGroup(noticeList, noticeIdSetInContent, errorNotices);
		Set<Subscribe> subscribeSet = getSubscribeSet(filterNoticeJsoupGroup.keySet());

		// Map<Long, List<Device>> userDeviceGroup = getFcmTokenGroup(subscribeSet);

		// fcmService.sendRssNotification(filterNoticeJsoupGroup, subscribeSet, userDeviceGroup);
		updateMaxPubData(filterNoticeJsoupGroup);

	}

	private Map<Notice, List<CrawlingData>> getFilterNoticeGroup(
		List<Notice> noticeList,
		Set<Long> noticeIdSetInContent,
		Map<Notice, String> errorNotices) {

		Map<Notice, List<CrawlingData>> filterNoticeGroup = noticeList.stream()
			.collect(groupingBy(notice -> notice,
				flatMapping( notice -> getJsoupData(notice, noticeIdSetInContent, errorNotices).stream(), toList())
			));

		return filterNoticeGroup.entrySet().stream()
			.filter(entry -> !entry.getValue().isEmpty())
			.collect(toMap(Map.Entry::getKey,
				Map.Entry::getValue));
	}

	private List<CrawlingData> getJsoupData(Notice notice, Set<Long> noticeIdSetInContent, Map<Notice, String> errorNotices) {
		boolean isInit = !noticeIdSetInContent.contains(notice.getId());

		try{
			List<CrawlingData> jsoupDatas = jsoupReader.getMechanicNoticeData(notice.getLink(), notice.getLastNoticeTime(), isInit);
			List<CrawlingData> filteredJsoupDatas = jsoupDatas.stream()
				.filter(jsoupData -> jsoupData.pubDate().isAfter(notice.getLastNoticeTime()))
				.toList();
			if (isInit) {
				contentWriteService.contentsSave(jsoupDatas, notice);
			}else {
				contentWriteService.contentsSave(filteredJsoupDatas, notice);
			}
			return filteredJsoupDatas;
		} catch (Exception e) {
			errorNotices.put(notice, e.getMessage());
			return List.of();
		}
	}

	private Set<Subscribe> getSubscribeSet(Set<Notice> notices) {
		return subscribeReadService.getSubscribeByNoticeIdIn(notices.stream()
			.map(Notice::getId)
			.toList());
	}

	private Map<Long, List<Device>> getFcmTokenGroup(Set<Subscribe> subscribeSet) {
		Set<Long> userIdSet = subscribeSet.stream()
			.map(subscribe -> subscribe.getUser().getId())
			.collect(toSet());

		List<Device> deviceList = deviceReadService.findByUserIdIn(userIdSet);

		return deviceList.stream()
			.collect(groupingBy(device -> device.getUser().getId(),
				mapping(device -> device, toList())));
	}

	private void updateMaxPubData(Map<Notice, List<CrawlingData>> filterNoticeJsoupGroup) {
		Map<Notice, LocalDateTime> maxPubDateNotice = filterNoticeJsoupGroup.entrySet().stream()
			.collect(toMap( Map.Entry::getKey,
				map -> map.getValue().stream()
					.map(CrawlingData::pubDate)
					.max(LocalDateTime::compareTo)
					.orElse(map.getKey().getLastNoticeTime()))
			);

		for(Map.Entry<Notice, LocalDateTime> map: maxPubDateNotice.entrySet()) {
			Notice notice = map.getKey();
			notice.changeLastNoticeTime(map.getValue());
		}
		noticeWriteService.saveAllAndFlush(maxPubDateNotice.keySet());
	}
}
