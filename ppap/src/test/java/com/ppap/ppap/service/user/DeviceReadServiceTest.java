package com.ppap.ppap.service.user;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.repository.DeviceJpaRepository;
import com.ppap.ppap.domain.user.service.DeviceReadService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@DisplayName("디바이스 Read 서비스 테스트")
@ExtendWith({MockitoExtension.class})
public class DeviceReadServiceTest {
    @InjectMocks
    private DeviceReadService deviceReadService;
    
    @Mock
    private DeviceJpaRepository deviceJpaRepository;

    private final DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("FindByUserIdIn 테스트")
    @Nested
    class FindByUserIdIn{
        @DisplayName("success")
        @Test
        void success() {
            // given
            List<User> userList = List.of(
                    dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L),
                    dummyEntity.getTestUser("rjsdnxogh12@naver.com", 2L),
                    dummyEntity.getTestUser("rjsdnxogh3@gmail.com", 3L),
                    dummyEntity.getTestUser("rjsdnxogh4@kakao.com", 4L)
            );

            Set<Long> userIdSet = userList.stream().map(User::getId).collect(Collectors.toSet());

            List<Device> deviceList = List.of(
                    Device.of(userList.get(0), "test1"),
                    Device.of(userList.get(0), "test2"),
                    Device.of(userList.get(1), "test3"),
                    Device.of(userList.get(2), "test4"),
                    Device.of(userList.get(3), "test5")
            );

            // mock
            given(deviceJpaRepository.findByUserIdIn(userIdSet)).willReturn(deviceList);

            // when
            List<Device> results = deviceReadService.findByUserIdIn(userIdSet);

            // then
            Assertions.assertEquals(deviceList.size(), results.size());
            verify(deviceJpaRepository).findByUserIdIn(userIdSet);
        }
    }
}
