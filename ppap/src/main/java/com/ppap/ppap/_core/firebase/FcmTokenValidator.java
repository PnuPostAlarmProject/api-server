package com.ppap.ppap._core.firebase;

import java.util.List;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception500;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmTokenValidator {
    private final FirebaseMessaging firebaseMessaging;

    public void validateToken(String fcmToken) {
        Message message = getFcmTestMessage(fcmToken);
        try{
            firebaseMessaging.send(message, true); // true를 설정하여 dry run을 활성화합니다.
        } catch (FirebaseMessagingException e) {
            throw new Exception400(BaseExceptionStatus.DEVICE_FCM_TOKEN_INVALID);
        } catch (Exception e) {
            throw new Exception500(BaseExceptionStatus.DEVICE_FCM_TOKEN_UNKNOWN_ERROR);
        }
    }

    public BatchResponse validateTokenThrowException(List<String> fcmTokenList) throws FirebaseMessagingException{
        List<Message> messageList = fcmTokenList.stream()
                .map(this::getFcmTestMessage)
                .toList();

        return firebaseMessaging.sendEach(messageList);
    }

    private Message getFcmTestMessage(String fcmToken) {
        return Message.builder()
            .setToken(fcmToken)
            .putData("testKey", "testBody")
            .build();
    }
}
