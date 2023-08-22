package com.ppap.ppap._core.firebase;

import com.google.firebase.auth.FirebaseToken;
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
    private final String FCM_VALIDATE_URL = "https://iid.googleapis.com/iid/info/";
    private final FirebaseMessaging firebaseMessaging;

    public void validateToken(String fcmToken) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .putData("testKey", "testBody")
                .build();
        try{
            firebaseMessaging.send(message, true); // true를 설정하여 dry run을 활성화합니다.
        } catch (FirebaseMessagingException e) {
            throw new Exception400(BaseExceptionStatus.DEVICE_FCM_TOKEN_INVALID);
        } catch (Exception e) {
            throw new Exception500(BaseExceptionStatus.DEVICE_FCM_TOKEN_UNKNOWN_ERROR);
        }
    }

}
