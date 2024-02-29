package com.ppap.ppap._core.firebase.message;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.ppap.ppap._core.crawler.CrawlingData;
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

    public void sendNotification(Map<Notice, List<CrawlingData>> filterNoticeCrawlingGroup,
                                    Set<Subscribe> subscribeSet,
                                    Map<Long, List<Device>> userDeviceGroup) {

        if (userDeviceGroup.keySet().isEmpty())
            return ;

        List<List<Message>> chunkMessages = getChunkMessages(filterNoticeCrawlingGroup, subscribeSet, userDeviceGroup);
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
            .toList();

        // 두 개로 나눈 이유는 하나의 파이프라인에서 처리할 시 동기적으로 처리하게 되는 문제가 발생한다.
        // List<BatchResponse> responseList = futureList.stream()
        //     .map(CompletableFuture::join)
        //     .filter(Objects::nonNull)
        //     .toList();
        //
        // responseList.stream()
        //     .flatMap(batchResponse -> batchResponse.getResponses().stream())
        //     .forEach(response -> {
        //         if (!response.isSuccessful()) {
        //             log.error(response.getException().getMessagingErrorCode().toString());
        //         }
        //     });
    }

    private List<List<Message>> getChunkMessages(Map<Notice, List<CrawlingData>> filterNoticeCrawlingGroup,
                                                Set<Subscribe> subscribeSet,
                                                Map<Long, List<Device>> userDeviceGroup) {

        Map<Long, List<CrawlingData>> filterNoticeIdCrawlingGroup = filterNoticeCrawlingGroup.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().getId(),
                Map.Entry::getValue));

        List<Message> messageList = createMessages(subscribeSet, filterNoticeIdCrawlingGroup, userDeviceGroup);

        return IntStream.iterate(0, i -> i < messageList.size(), i -> i + CHUNK_SIZE)
            .mapToObj(i -> messageList.subList(i, Math.min(i + CHUNK_SIZE, messageList.size())))
            .toList();
    }

    private List<Message> createMessages(Set<Subscribe> subscribeSet,
                                        Map<Long, List<CrawlingData>> filterNoticeIdCrawlingGroup,
                                        Map<Long, List<Device>> userDeviceGroup) {

        return forkJoinPool.submit(() -> subscribeSet.parallelStream()
            .flatMap(subscribe -> createMessageForSubscribe(subscribe, filterNoticeIdCrawlingGroup, userDeviceGroup))
            .toList()
        ).join();
    }

    private Stream<Message> createMessageForSubscribe(Subscribe subscribe,
                                                    Map<Long, List<CrawlingData>> filterNoticeIdCrawlingGroup,
                                                    Map<Long, List<Device>> userDeviceGroup) {

        List<CrawlingData> crawlingDataList = filterNoticeIdCrawlingGroup.getOrDefault(subscribe.getNotice().getId(), Collections.emptyList());
        List<Device> devices = userDeviceGroup.getOrDefault(subscribe.getUser().getId(), Collections.emptyList());
        return crawlingDataList.stream()
            .flatMap(crawlingData -> createMessagesForCrawlingData(subscribe, crawlingData, devices).stream());
    }

    private List<Message> createMessagesForCrawlingData(Subscribe subscribe, CrawlingData crawlingData, List<Device> devices) {
        return devices.stream()
            .map(device -> Message.builder()
                .setToken(device.getFcmToken())
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                        .setTitle(subscribe.getTitle())
                        .setBody(crawlingData.title())
                        .build())
                    .build())
                .putData("link", crawlingData.link())
                .build())
            .toList();
    }
}
