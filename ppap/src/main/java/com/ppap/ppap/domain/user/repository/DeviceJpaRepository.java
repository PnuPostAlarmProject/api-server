package com.ppap.ppap.domain.user.repository;

import com.ppap.ppap.domain.user.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DeviceJpaRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserIdIn(Collection<Long> userIds);

    boolean existsByFcmToken(String fcmToken);
}
