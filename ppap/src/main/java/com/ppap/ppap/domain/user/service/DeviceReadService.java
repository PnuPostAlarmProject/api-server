package com.ppap.ppap.domain.user.service;

import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.repository.DeviceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DeviceReadService {
    private final DeviceJpaRepository deviceJpaRepository;

    public List<Device> findByUserIdIn(Collection<Long> userIds) {
        return deviceJpaRepository.findByUserIdIn(userIds);
    }
}
