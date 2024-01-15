package com.ppap.ppap._core.scheduler;

import com.ppap.ppap._core.firebase.message.FcmService;
import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap._core.rss.RssReader;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.NoticeJpaRepository;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import com.ppap.ppap.domain.subscribe.service.ContentWriteService;
import com.ppap.ppap.domain.subscribe.service.NoticeReadService;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.service.DeviceReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RssSchedulerService {
    private final ForkJoinPool forkJoinPool;
    private final RssReader rssReader;
    private final NoticeReadService noticeReadService;
    private final SubscribeReadService subscribeReadService;
    private final DeviceReadService deviceReadService;
    private final ContentReadService contentReadService;
    private final ContentWriteService contentWriteService;
    private final NoticeJpaRepository noticeJpaRepository;
    private final FcmService fcmService;


    @Scheduled(cron = "0 0/10 * * * ?", zone="Asia/Seoul")
    public void run() {
        log.info("cron Start");
        Long start = System.currentTimeMillis();

        processRssData();

        Long end = System.currentTimeMillis();
        log.info("실행시간 : " + (end-start) + "ms");
        log.info("cron end");
    }

    private void processRssData() {
        List<Notice> noticeList = noticeReadService.findAll();
        Set<Long> noticeIdSetInContent = contentReadService.findDistinctNoticeId();
        Map<Notice, String> errorNotices = new HashMap<>();

        // 각 noticeId에 대해 읽어 공지사항별 RSS데이터 그룹을 만든다.
        Map<Notice, List<RssData>> filterNoticeRssGroup = getFilterNoticeGroup(noticeList, errorNotices, noticeIdSetInContent);

        System.out.println("===========filterNoticeRssGroup==========");
        System.out.println(filterNoticeRssGroup);

        // 필요한 구독 목록 중복없이 다 가져옴.
        Set<Subscribe> subscribeSet = getSubscribeSet(filterNoticeRssGroup.keySet());
        System.out.println("===========subscribeSet==========");
        System.out.println(subscribeSet);

        // userId별 Fcm토큰 값들을 조회
        System.out.println("===========userDeviceGroup==========");
        Map<Long, List<Device>> userDeviceGroup = getFcmTokenGroup(subscribeSet);
        System.out.println(userDeviceGroup);


        // 가져온 FCM 토큰들을 통해 알림 전송
        fcmService.sendRssNotification(filterNoticeRssGroup, subscribeSet, userDeviceGroup);

        // 각 공지사항 중, 가장 최근 데이터의 발행시각을 가져온 뒤 디비에 업데이트
        updateMaxPubDateNotice(filterNoticeRssGroup);

        System.out.println("===========errorNotices============");
        System.out.println(errorNotices);
    }

    /**
     * 데이터베이스에 저장된 공지사항 rss링크들을 통해 학교 공지사항 RSS 데이터를 받아오는 메소드
     * @param noticeList : 데이터베이스에 들어있는 전체 공지사항
     * @param errorNotices : 에러가 발생한 공지사항
     * @return : 공지사항Id별 RssData
     */
    private Map<Notice, List<RssData>> getFilterNoticeGroup(List<Notice> noticeList, Map<Notice, String> errorNotices,
                                                          Set<Long> noticeIdSetInContent) {
        Map<Notice, List<RssData>> filterNoticeGroup = forkJoinPool.submit(() -> noticeList.parallelStream()
                .collect(groupingBy(notice -> notice,
                        flatMapping(notice -> getRssData(notice, errorNotices, noticeIdSetInContent).stream(), toList()))
                )).join();

        return filterNoticeGroup.entrySet().stream()
                .filter(map -> !map.getValue().isEmpty())
                .collect(toMap(Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    /**
     * 받은 rss링크를 통해 Rss 데이터를 가져온 뒤 갱신해야 하는 데이터를 반환하는 코드
     * 만약 처음 공지사항을 받아온다면 30개를 저장하고 아니라면 갱신해야하는 데이터만 디비에 저장한다.
     * @param notice : 공지사항
     * @param errorNotice : 에러가 발생한 공지사항
     * @return 알림을 줘야하는(갱신된) rss 데이터
     */
    private List<RssData> getRssData(Notice notice, Map<Notice, String> errorNotice, Set<Long> noticeIdSetInContent) {
        boolean isInit = !noticeIdSetInContent.contains(notice.getId());
        try{
            List<RssData> rssDataList = rssReader.getRssData(notice.getRssLink(), isInit);
            List<RssData> filterRssDataList = rssDataList.stream()
                    .filter(rssData -> rssData.pubDate().isAfter(notice.getLastNoticeTime())).toList();

            // 처음 받아오는 거라면 30개를 저장, 아니라면 서버에 저장된 시간보다 뒤에 있는 값만 저장.
            if(isInit) {
                contentWriteService.contentsSave(rssDataList, notice);
            } else{
                contentWriteService.contentsSave(filterRssDataList, notice);
            }

            return filterRssDataList;
        }catch (Exception e) {
            // 에러가 발생하면 errorNotice에 저장하고, 이후 관리자에게 메일을 보낼 수 있도록 한다.
            errorNotice.put(notice, e.getMessage());
            return List.of();
        }
    }


    // 공지사항 최근 날짜 업데이트
    private void updateMaxPubDateNotice(Map<Notice, List<RssData>> filterNoticeRssGroup) {
        Map<Notice, LocalDateTime> maxPubDateNotice = filterNoticeRssGroup.entrySet().stream()
                .collect(toMap( Map.Entry::getKey,
                        map -> map.getValue().stream()
                                .map(RssData::pubDate)
                                .max(LocalDateTime::compareTo)
                                .orElse(map.getKey().getLastNoticeTime()))
                );

        for(Map.Entry<Notice, LocalDateTime> map: maxPubDateNotice.entrySet()) {
            Notice notice = map.getKey();
            notice.changeLastNoticeTime(map.getValue());
        }
        noticeJpaRepository.saveAllAndFlush(maxPubDateNotice.keySet());

        // 데이터베이스에 비동기 update는 트랜잭션 락 때문에 위험해 사용하지 않았습니다.
//        forkJoinPool.submit(() -> maxPubDateNotice.entrySet().parallelStream()
//                .forEach((map) -> {
//                    Notice notice = map.getKey();
//                    notice.changeLastNoticeTime(map.getValue());
//                    noticeJpaRepository.saveAndFlush(notice);
//        })).join();
    }

    // 리팩토링 필요
    private Set<Subscribe> getSubscribeSet(Set<Notice> noticeSet) {
        return forkJoinPool.submit(() -> noticeSet.parallelStream()
                .map(Notice::getId)
                .flatMap(noticeId -> subscribeReadService.getSubscribeByNoticeId(noticeId).stream())
                .collect(toSet()))
                .join();
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
