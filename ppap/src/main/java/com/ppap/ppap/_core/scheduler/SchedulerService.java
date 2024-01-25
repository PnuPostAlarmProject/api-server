package com.ppap.ppap._core.scheduler;

import com.ppap.ppap._core.crawler.CrawlingData;
import com.ppap.ppap._core.crawler.JsoupReader;
import com.ppap.ppap._core.firebase.message.FcmService;
import com.ppap.ppap._core.crawler.RssReader;
import com.ppap.ppap._core.utils.EmailService;
import com.ppap.ppap._core.utils.MDCUtils;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import com.ppap.ppap.domain.subscribe.service.ContentWriteService;
import com.ppap.ppap.domain.subscribe.service.NoticeReadService;
import com.ppap.ppap.domain.subscribe.service.NoticeWriteService;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.service.DeviceReadService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import static java.util.stream.Collectors.*;

@Profile({"dev", "stage", "prod"})
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SchedulerService {
    private final ForkJoinPool forkJoinPool;
    private final RssReader rssReader;
    private final JsoupReader jsoupReader;
    private final NoticeReadService noticeReadService;
    private final NoticeWriteService noticeWriteService;
    private final SubscribeReadService subscribeReadService;
    private final DeviceReadService deviceReadService;
    private final ContentReadService contentReadService;
    private final ContentWriteService contentWriteService;
    private final FcmService fcmService;
    private final EmailService emailService;

    @Value("${spring.mail.admin}")
    private String toEmail;

    public void run() {
        MDC.put("logFileName", "schedule");
        log.info("cron Start");
        long start = System.currentTimeMillis();
        try {
            processData();
        } finally {
            log.info("실행시간 : {} ms", System.currentTimeMillis() - start);
            log.info("cron end");
            MDC.clear();
        }
    }

    private void processData() {
        List<Notice> noticeList = noticeReadService.findAll();

        Set<Long> noticeIdSetInContent = contentReadService.findDistinctNoticeIdIn(noticeList);

		// 멀티 쓰레드 환경에서 실행되므로 Thread Safe한 자료구조를 사용해야한다.
		ConcurrentHashMap<Notice, String> errorNotices = new ConcurrentHashMap<>();

        // 각 noticeId에 대해 읽어 공지사항별 공지사항 데이터 그룹을 만든다.
        Map<Notice, List<CrawlingData>> filterNoticeCrawlingGroup = getFilterNoticeGroup(noticeList, errorNotices, noticeIdSetInContent);

        // 필요한 구독 목록 중복없이 다 가져옴.
        Set<Subscribe> subscribeSet = getSubscribeSet(filterNoticeCrawlingGroup.keySet());

        // userId별 Fcm토큰 값들을 조회
        Map<Long, List<Device>> userDeviceGroup = getFcmTokenGroup(subscribeSet);

        // 가져온 FCM 토큰들을 통해 알림 전송
        fcmService.sendNotification(filterNoticeCrawlingGroup, subscribeSet, userDeviceGroup);

        // 각 공지사항 중, 가장 최근 데이터의 발행시각을 가져온 뒤 디비에 업데이트
        updateMaxPubDateNotice(filterNoticeCrawlingGroup);

        if (!errorNotices.isEmpty()) {
            try {
                emailService.sendEmailForSchdulerErrorLog(errorNotices, toEmail);
            } catch (MessagingException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 데이터베이스에 저장된 공지사항 링크들을 통해 학교 공지사항 데이터를 받아오는 메소드
     * @param noticeList : 데이터베이스에 들어있는 전체 공지사항
     * @param errorNotices : 에러가 발생한 공지사항
     * @return : 공지사항Id별 크롤링 데이터
     */
    private Map<Notice, List<CrawlingData>> getFilterNoticeGroup(List<Notice> noticeList, Map<Notice, String> errorNotices,
                                                          Set<Long> noticeIdSetInContent) {

        Map<Notice, List<CrawlingData>> crawlingNoticeGroup = forkJoinPool.submit(
            new MDCUtils.MDCAwareCallable<>( () -> noticeList.parallelStream()
                .collect(groupingBy(notice -> notice,
                    flatMapping(notice -> getCrawlingData(notice, errorNotices, noticeIdSetInContent).stream(), toList()))
                ))).join();

		// 처음 받아오는 거라면 전체 저장, 아니라면 서버에 저장된 시간보다 뒤에 있는 값만 저장.
        contentWriteService.contentAllSave(crawlingNoticeGroup);

		Map<Notice, List<CrawlingData>> filterCrawlingNoticeGroup =
			crawlingNoticeGroup.entrySet().stream()
				.collect(groupingBy(Map.Entry::getKey,
					flatMapping((entry -> entry.getValue().stream()
							.filter(crawlingData -> crawlingData.pubDate().isAfter(entry.getKey().getLastNoticeTime()))
						),
						toList()))
				);

		filterCrawlingNoticeGroup.keySet().removeIf(key -> filterCrawlingNoticeGroup.get(key).isEmpty());

        return filterCrawlingNoticeGroup;
    }

    /**
     * 받은 링크를 통해 공지사항 데이터를 가져온 뒤 갱신해야 하는 데이터를 반환하는 코드
     * 만약 처음 공지사항을 받아온다면 30개를 저장하고 아니라면 갱신해야하는 데이터만 디비에 저장한다.
     * @param notice : 공지사항
     * @param errorNotice : 에러가 발생한 공지사항
     * @return 알림을 줘야하는(갱신된) 공지사항 데이터
     */
    private List<CrawlingData> getCrawlingData(Notice notice, Map<Notice, String> errorNotice, Set<Long> noticeIdSetInContent) {
        boolean isInit = !noticeIdSetInContent.contains(notice.getId());
        List<CrawlingData> crawlingDataList = new ArrayList<>();
        try{
            switch (notice.getNoticeType()) {
                case RSS -> crawlingDataList = rssReader.getRssData(notice.getLink(), notice.getLastNoticeTime(), isInit);
                case JSOUP -> crawlingDataList = jsoupReader.getMechanicNoticeData(notice.getLink(), notice.getLastNoticeTime(), isInit);
            }

            return crawlingDataList;
        }catch (Exception e) {
            // 에러가 발생하면 errorNotice에 저장하고, 이후 관리자에게 메일을 보낼 수 있도록 한다.
            errorNotice.put(notice, e.getMessage());
            return List.of();
        }
    }


    // 공지사항 최근 날짜 업데이트
    private void updateMaxPubDateNotice(Map<Notice, List<CrawlingData>> filterNoticeCrawlingGroup) {
        Map<Notice, LocalDateTime> maxPubDateNotice = filterNoticeCrawlingGroup.entrySet().stream()
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

        noticeWriteService.updateAll(maxPubDateNotice.keySet());

    }

    private Set<Subscribe> getSubscribeSet(Set<Notice> noticeSet) {
		return subscribeReadService.getSubscribeByNoticeIdIn(noticeSet.stream()
			.map(Notice::getId)
			.toList());
    }

    // userId별 기기 그룹 조회
    private Map<Long, List<Device>> getFcmTokenGroup(Set<Subscribe> subscribeSet) {

        Set<Long> userIdSet = subscribeSet.stream()
                .map(subscribe -> subscribe.getUser().getId())
                .collect(toSet());

        List<Device> deviceList = deviceReadService.findByUserIdIn(userIdSet);

        return deviceList.stream()
                .collect(groupingBy(device -> device.getUser().getId(),
                        mapping(device -> device, toList())));
    }
}
