package com.ppap.ppap.domain.user.service;

import com.ppap.ppap._core.firebase.FcmTokenValidator;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.repository.DeviceJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DeviceWriteService {
    private final DeviceJpaRepository deviceJpaRepository;
    private final FcmTokenValidator fcmTokenValidator;

    public Device save(User user, String fcmToken) {
        return deviceJpaRepository.findByFcmToken(fcmToken).orElseGet(() -> {
            fcmTokenValidator.validateToken(fcmToken);
            return deviceJpaRepository.save(Device.of(user, fcmToken));
        });
    }
}
