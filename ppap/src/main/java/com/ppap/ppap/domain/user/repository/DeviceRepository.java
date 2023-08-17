package com.ppap.ppap.domain.user.repository;

import com.ppap.ppap.domain.user.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
