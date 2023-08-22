package com.ppap.ppap.service.user;

import com.ppap.ppap.domain.user.service.DeviceWriteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("디바이스 Write 서비스 테스트")
@ExtendWith({MockitoExtension.class})
public class DeviceWriteServiceTest {
    @InjectMocks
    private DeviceWriteService deviceWriteService;
}
