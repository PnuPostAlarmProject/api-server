package com.ppap.ppap._core.firebase.message;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.user.entity.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendRssNotification(Map<Long, List<RssData>> filterNoticeRssGroup,
                                    Set<Subscribe> subscribeSet,
                                    Map<Long, List<Device>> userDeviceGroup) {
        // FCM 푸시 메세지 보내기.
    }

    public String sendNotification() {
        Notification notification = Notification.builder()
                .setTitle("테스트용 타이틀")
                .setBody("테스트용 내용")
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .build();
        return "ok";
    }
}
