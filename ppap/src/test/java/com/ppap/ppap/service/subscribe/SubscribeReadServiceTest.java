package com.ppap.ppap.service.subscribe;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception403;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.subscribe.dto.SubscribeGetListResponseDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @DisplayName("유저ID로 구독 리스트 조회 테스트")
    @Nested
    class GetSubscribeListTest {

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
            List<SubscribeGetListResponseDto> resultDtos = subscribeReadService.getSubscribeList(user);

            // then
            assertEquals(testSubscribeList.size(), resultDtos.size());
            for(int i=0; i<testSubscribeList.size(); i++) {
                assertEquals(i+1, resultDtos.get(i).subscribeId());
                assertEquals("테스트 " + (i+1), resultDtos.get(i).title());
                assertEquals(true, resultDtos.get(i).isActive());
            }
        }
    }

    @DisplayName("공지ID로 구독리스트 조회 테스트")
    @Nested
    class GetSubscribeByNoticeIdTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User user = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Subscribe> testSubscribeList = dummyEntity.getTestSubscribeList(user, testNoticeList).stream()
                    .filter(subscribe -> subscribe.getNotice().getId().equals(testNoticeList.get(0).getId()))
                    .toList();

            // mock
            given(subscribeJpaRepository.findByNoticeId(testNoticeList.get(0).getId())).willReturn(testSubscribeList);

            // when
            List<Subscribe> resultDtos = subscribeReadService.getSubscribeByNoticeId(testNoticeList.get(0).getId());

            // then
            assertEquals(1, resultDtos.size());
            assertEquals(1L, resultDtos.get(0).getId());
        }
    }

    @DisplayName("유저ID로 구독 엔티티 리스트 조회 테스트")
    @Nested
    class GetSubscribeEntityListTest {

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
            List<Subscribe> resultDtos = subscribeReadService.getSubscribeEntityList(user);

            // then
            assertEquals(testSubscribeList.size(), resultDtos.size());
            for(int i=0; i<testSubscribeList.size(); i++) {
                assertEquals(i+1, resultDtos.get(i).getId());
                assertEquals("테스트 " + (i+1), resultDtos.get(i).getTitle());
                assertEquals(true, resultDtos.get(i).getIsActive());
            }
        }

        @DisplayName("실패 빈 구독 목록")
        @Test
        void fail_subscribe_empty() {
            // given
            User user = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);

            // mock
            given(subscribeJpaRepository.findByUserId(user.getId())).willReturn(List.of());

            // when
            Throwable exception = assertThrows(Exception404.class, () -> subscribeReadService.getSubscribeEntityList(user));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_EMPTY.getMessage(), exception.getMessage());

        }
    }

    @DisplayName("단일 구독 조회 테스트")
    @Nested
    class GetSubscribeTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            Subscribe testSubscribe = dummyEntity.getTestSubscribeList(testUser, testNoticeList).get(0);

            // mock
            given(subscribeJpaRepository.findByIdFetchJoinNotice(testSubscribe.getId()))
                    .willReturn(Optional.of(testSubscribe));

            // when
            SubscribeGetResponseDto response = subscribeReadService.getSubscribe(testUser, testSubscribe.getId());

            // then
            assertEquals(testSubscribe.getId(), response.subscribeId());
            assertEquals(testSubscribe.getTitle(), response.title());
            assertEquals(testSubscribe.getNoticeLink(), response.noticeLink());
            assertEquals(testSubscribe.getNotice().getRssLink(), response.rssLink());
            assertEquals(testSubscribe.getIsActive(), response.isActive());
        }

        @DisplayName("실패 존재하지 않는 구독")
        @Test
        void fail_subscribe_not_found() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            Subscribe testSubscribe = dummyEntity.getTestSubscribeList(testUser, testNoticeList).get(0);

            // mock
            given(subscribeJpaRepository.findByIdFetchJoinNotice(testSubscribe.getId()))
                    .willReturn(Optional.empty());

            // when
            Throwable exception = assertThrows(Exception404.class,
                    () -> subscribeReadService.getSubscribe(testUser, testSubscribe.getId()));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 작성자가 아닌 사람의 요청")
        @Test
        void fail_request_not_writer() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            User anotherUser = dummyEntity.getTestUser("rjsdnxogh12@gmail.com", 2L);
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            Subscribe testSubscribe = dummyEntity.getTestSubscribeList(testUser, testNoticeList).get(0);

            // mock
            given(subscribeJpaRepository.findByIdFetchJoinNotice(testSubscribe.getId()))
                    .willReturn(Optional.of(testSubscribe));

            // when
            Throwable exception = assertThrows(Exception403.class,
                    () -> subscribeReadService.getSubscribe(anotherUser, testSubscribe.getId()));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getMessage(), exception.getMessage());
        }
    }
}
