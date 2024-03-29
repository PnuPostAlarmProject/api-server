package com.ppap.ppap._core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.InputStream;

@Profile({"dev", "stage", "prod"})
@Configuration
public class FcmConfig {

    @Bean
    public FirebaseMessaging initializeFirebaseApp() throws IOException {

        FileSystemResource resource = new FileSystemResource("./src/main/resources/ppap_fcmkey.json");
        InputStream inputStream = resource.getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                // .setConnectTimeout(10000)
                // .setReadTimeout(10000)
                .build();

        if (FirebaseApp.getApps().isEmpty())
            FirebaseApp.initializeApp(options);

        return FirebaseMessaging.getInstance();
    }
}
