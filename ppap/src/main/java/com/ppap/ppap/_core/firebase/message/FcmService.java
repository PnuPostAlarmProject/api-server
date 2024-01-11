package com.ppap.ppap._core.firebase.message;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.user.entity.Device;
import io.netty.util.concurrent.CompleteFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendRssNotification(Map<Notice, List<RssData>> filterNoticeRssGroup,
                                    Set<Subscribe> subscribeSet,
                                    Map<Long, List<Device>> userDeviceGroup) {
        // FCM 푸시 메세지 보내기.
        Notification notification = Notification.builder()
            .setTitle("새로운 공지사항이 등록되었습니다.")
            .build();

        List<Message> messageList = new ArrayList<>();
        for (List<Device> devices : userDeviceGroup.values()) {
            devices.forEach((device) -> {
                    messageList.add(Message.builder()
                        .setNotification(notification)
                        .setToken(device.getFcmToken())
                        .putData("click_action", "NOTIFICATION_CLICK")
                        // .putData("deeplink", "APP_URL")
                        .build());
                });
        }

        List<List<Message>> chunkMessages = devideMessageList(messageList, 500);

        final ExecutorService executor = Executors.newFixedThreadPool(Math.min(chunkMessages.size(), 10),
            r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

        // 아래를 비동기(CompletableFuture) 프로그래밍으로 변경해보기
        List<CompletableFuture<BatchResponse>> futureList = chunkMessages.stream()
            .map(messages -> CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return firebaseMessaging.sendEach(messages);
                    } catch (FirebaseMessagingException e) {
                        log.error(e.getMessage());
                        return null;
                    }
                },executor))
            .map(future -> future.exceptionally(ex -> {
                log.error(ex.getMessage());
                return null;
            }))
            .toList();

        // 두 개로 나눈 이유는 하나의 파이프라인에서 처리할 시 동기적으로 처리하게 되는 문제가 발생한다.
        List<BatchResponse> responseList = futureList.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .toList();

        // executor.shutdown();
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

    private List<List<Message>> devideMessageList(List<Message> messageList, int size) {
        return IntStream.iterate(0, i -> i < messageList.size(), i -> i + size)
            .mapToObj(i -> messageList.subList(i, Math.min(i + size, messageList.size())))
            .toList();
    }
}
