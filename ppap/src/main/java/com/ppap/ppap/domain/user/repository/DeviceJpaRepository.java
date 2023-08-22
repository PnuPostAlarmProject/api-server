package com.ppap.ppap.domain.user.repository;

import com.ppap.ppap.domain.user.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeviceJpaRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserIdIn(Collection<Long> userIds);

    Optional<Device> findByFcmToken(String fcmToken);
}
