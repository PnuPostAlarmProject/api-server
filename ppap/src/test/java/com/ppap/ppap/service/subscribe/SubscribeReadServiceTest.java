package com.ppap.ppap.service.subscribe;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap.domain.subscribe.dto.SubscribeGetResponseDto;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@DisplayName("구독 Read 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class SubscribeReadServiceTest{
    @InjectMocks
    private SubscribeReadService subscribeReadService;

    @Mock
    private SubscribeJpaRepository subscribeJpaRepository;

    private final DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("유저ID로 구독 조회 테스트")
    @Nested
    class FindByUserIdTest {

        @DisplayName("성공")
        @Test
        void success() {
            // given
            User user = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Subscribe> testSubscribeList = dummyEntity.getTestSubscribeList(user, testNoticeList);

            // mock
            given(subscribeJpaRepository.findByUserId(user.getId())).willReturn(testSubscribeList);

            // when
            List<SubscribeGetResponseDto> resultDtos = subscribeReadService.getSubscribe(user);

            // then
            assertEquals(testSubscribeList.size(), resultDtos.size());
            for(int i=0; i<testSubscribeList.size(); i++) {
                assertEquals(i+1, resultDtos.get(i).subscribeId());
                assertEquals("테스트 " + (i+1), resultDtos.get(i).title());
                assertEquals(true, resultDtos.get(i).isActive());
            }
        }
    }

}
