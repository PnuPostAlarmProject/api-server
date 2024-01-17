package com.ppap.ppap._core.firebase.message;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap._core.utils.MDCUtils;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.user.entity.Device;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;
    private final ForkJoinPool forkJoinPool;
    private static final int CHUNK_SIZE = 500;

    public void sendRssNotification(Map<Notice, List<RssData>> filterNoticeRssGroup,
                                    Set<Subscribe> subscribeSet,
                                    Map<Long, List<Device>> userDeviceGroup) {

        if (userDeviceGroup.keySet().isEmpty())
            return ;

        List<List<Message>> chunkMessages = getChunkMessages(filterNoticeRssGroup, subscribeSet, userDeviceGroup);
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(chunkMessages.size(), 10),
            r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

        List<CompletableFuture<BatchResponse>> futureList = chunkMessages.stream()
            .map(messages -> CompletableFuture.supplyAsync(
                new MDCUtils.MDCAwareSupplier<>(
                    () -> {
                        try {
                            return firebaseMessaging.sendEach(messages);
                        } catch (FirebaseMessagingException e) {
                            log.error(e.getMessage());
                            return null;
                        }
                }),executor))
            .map(future -> future.exceptionally(
                new MDCUtils.MDCAwareFunction<>( ex -> {
                    log.error(ex.getMessage());
                    return null;
            })))
            .toList();

        // 두 개로 나눈 이유는 하나의 파이프라인에서 처리할 시 동기적으로 처리하게 되는 문제가 발생한다.
        List<BatchResponse> responseList = futureList.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .toList();

    }

    public String sendNotification() {
        Notification notification = Notification.builder()
                .setTitle("테스트용 타이틀")
                .setBody("테스트용 내용")
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken("czqDaETOQQCGd59IwTU4Nr:APA91bH0jhPPMEEneSDkHSCuqSbByWv5Ts8dUeD--Ueq1cdnLls_CRD7ut1qo6TFZNw1kagPwLMj3IrHFlH7MIWLGgq3KehFolRat1_kwAEIDecUmaTSDBA7gFAKwDNRPtlRT6uT5E1-")
                .build();

        ApiFuture<String> future = firebaseMessaging.sendAsync(message);
        future.addListener(() -> {
            if(future.isDone()) log.info("전송 성공");
            else log.error("전송 실패");
        }, Executors.newSingleThreadExecutor());
        return "ok";
    }

    private List<List<Message>> getChunkMessages(Map<Notice, List<RssData>> filterNoticeRssGroup,
                                                Set<Subscribe> subscribeSet,
                                                Map<Long, List<Device>> userDeviceGroup) {

        Map<Long, List<RssData>> filterNoticeIdRssGroup = filterNoticeRssGroup.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().getId(),
                Map.Entry::getValue));

        List<Message> messageList = createMessages(subscribeSet, filterNoticeIdRssGroup, userDeviceGroup);

        return IntStream.iterate(0, i -> i < messageList.size(), i -> i + CHUNK_SIZE)
            .mapToObj(i -> messageList.subList(i, Math.min(i + CHUNK_SIZE, messageList.size())))
            .toList();
    }

    private List<Message> createMessages(Set<Subscribe> subscribeSet,
                                        Map<Long, List<RssData>> filterNoticeIdRssGroup,
                                        Map<Long, List<Device>> userDeviceGroup) {

        return forkJoinPool.submit(() -> subscribeSet.parallelStream()
            .flatMap(subscribe -> createMessageForSubscribe(subscribe, filterNoticeIdRssGroup, userDeviceGroup))
            .toList()
        ).join();
    }

    private Stream<Message> createMessageForSubscribe(Subscribe subscribe,
                                                    Map<Long, List<RssData>> filterNoticeIdRssGroup,
                                                    Map<Long, List<Device>> userDeviceGroup) {

        List<RssData> rssDataList = filterNoticeIdRssGroup.getOrDefault(subscribe.getNotice().getId(), Collections.emptyList());
        List<Device> devices = userDeviceGroup.getOrDefault(subscribe.getUser().getId(), Collections.emptyList());
        return rssDataList.stream()
            .flatMap(rssData -> createMessagesForRssData(rssData, devices).stream());
    }

    private List<Message> createMessagesForRssData(RssData rssData, List<Device> devices) {

        Notification notification = Notification.builder()
            .setTitle(rssData.title())
            .build();
        return devices.stream()
            .map(device -> Message.builder()
                .setNotification(notification)
                .setToken(device.getFcmToken())
                .putData("click_action", "NOTIFICATION_CLICK")
                .build())
            .toList();
    }
}
