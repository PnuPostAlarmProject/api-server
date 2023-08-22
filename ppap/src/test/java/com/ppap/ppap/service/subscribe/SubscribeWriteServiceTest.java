package com.ppap.ppap.service.subscribe;


import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception403;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap._core.rss.RssReader;
import com.ppap.ppap._core.rss.UrlFactory;
import com.ppap.ppap.domain.subscribe.dto.SubscribeCreateRequestDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeUpdateRequestDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeUpdateResponseDto;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.subscribe.service.NoticeReadService;
import com.ppap.ppap.domain.subscribe.service.NoticeWriteService;
import com.ppap.ppap.domain.subscribe.service.SubscribeWriteService;
import com.ppap.ppap.domain.user.entity.User;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@DisplayName("구독 Write 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class SubscribeWriteServiceTest {
    @InjectMocks
    private SubscribeWriteService subscribeWriteService;
    @Mock
    private ObjectProvider<SAXBuilder> saxBuilderProvider;
    @Mock
    private UrlFactory urlFactory;
    @Mock
    private SubscribeJpaRepository subscribeJpaRepository;
    @Mock
    private NoticeWriteService noticeWriteService;
    @Mock
    private NoticeReadService noticeReadService;
    @Spy
    private RssReader rssReader = new RssReader(saxBuilderProvider, urlFactory);

    private DummyEntity dummyEntity = new DummyEntity();



    @DisplayName("구독 생성 테스트")
    @Nested
    class CreateTest {
        @DisplayName("성공 notice링크는 null")
        @Test
        void success_noticeLink_null() {
            // given
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto("테스트1", "https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do", null);
            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            // mock
            given(noticeReadService.findByRssLink(anyString())).willReturn(Optional.of(notice));
            given(subscribeJpaRepository.existsByUserAndNotice(any(), any())).willReturn(false);

            // when
            subscribeWriteService.create(requestDto, user);

            // then
            verify(subscribeJpaRepository).save(any());

        }

        @DisplayName("성공 notice 링크는 같은 rss 링크 학과")
        @Test
        void success_noticeLink_equal_rssLink_major() {
            // given
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto("테스트1",
                    "https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do",
                    "https://cse.pusan.ac.kr/cse/14651/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            // mock
            given(noticeReadService.findByRssLink(anyString())).willReturn(Optional.of(notice));
            given(subscribeJpaRepository.existsByUserAndNotice(any(), any())).willReturn(false);

            // when
            subscribeWriteService.create(requestDto, user);

            // then
            verify(subscribeJpaRepository).save(any());
        }

        @DisplayName("성공 Notice가 없는 경우")
        @Test
        void success_not_exist_notice() {
            // given
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto("테스트1",
                    "https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do",
                    "https://cse.pusan.ac.kr/cse/14651/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            // mock
            given(noticeReadService.findByRssLink(anyString())).willReturn(Optional.empty());
            willDoNothing().given(rssReader).validRssLink(anyString());
            given(subscribeJpaRepository.existsByUserAndNotice(any(), any())).willReturn(false);
            given(noticeWriteService.save(requestDto.rssLink())).willReturn(notice);

            // when
            subscribeWriteService.create(requestDto, user);

            // then
            verify(subscribeJpaRepository).save(any());
        }

        @DisplayName("실패 이미 존재하는 구독")
        @Test
        void fail_alread_exist_subscribe() {
            // given
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto("테스트1",
                    "https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do",
                    "https://eec.pusan.ac.kr/eec/12412/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            // mock
            given(noticeReadService.findByRssLink(anyString())).willReturn(Optional.of(notice));
            given(subscribeJpaRepository.existsByUserAndNotice(any(), any())).willReturn(true);

            // when
            Throwable exception = Assertions.assertThrows(Exception400.class, () -> subscribeWriteService.create(requestDto, user));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_ALREADY_EXIST.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 notice rss 링크 학과 불일치")
        @Test
        void fail_notice_major_not_eqaul_rss_major() {
            // given
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto("테스트1",
                    "https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do",
                    "https://eec.pusan.ac.kr/eec/12412/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            // mock
            given(noticeReadService.findByRssLink(anyString())).willReturn(Optional.of(notice));
            given(subscribeJpaRepository.existsByUserAndNotice(any(), any())).willReturn(false);

            // when
            Throwable exception = Assertions.assertThrows(Exception400.class, () -> subscribeWriteService.create(requestDto, user));

            // then
            assertEquals(BaseExceptionStatus.RSS_NOTICE_LINK_MISMATCH.getMessage(), exception.getMessage());
        }
    }

    @DisplayName("구독 수정 테스트")
    @Nested
    class UpdateTest{
        @DisplayName("성공 notice 링크는 null")
        @Test
        void success_noticeLink_null() {
            // given
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("테스트테스트1",
                    null);
            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);
            Subscribe subscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(subscribe.getId())).willReturn(Optional.of(subscribe));

            // when
            SubscribeUpdateResponseDto resultDto = subscribeWriteService.update(requestDto, subscribe.getId(), user);

            // then
            verify(subscribeJpaRepository).saveAndFlush(any());
            assertEquals(requestDto.title(), resultDto.title());
            assertEquals(requestDto.noticeLink(), resultDto.noticeLink());
        }

        @DisplayName("성공 notice 링크는 같은 rss 링크 학과")
        @Test
        void success_noticeLink_equal_rssLink_major() {
            // given
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("테스트테스트1",
                    "https://cse.pusan.ac.kr/cse/12412/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);
            Subscribe subscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(subscribe.getId())).willReturn(Optional.of(subscribe));

            // when
            SubscribeUpdateResponseDto resultDto = subscribeWriteService.update(requestDto, subscribe.getId(), user);

            // then
            verify(subscribeJpaRepository).saveAndFlush(any());
            assertEquals(requestDto.title(), resultDto.title());
            assertEquals(requestDto.noticeLink(), resultDto.noticeLink());
        }

        @DisplayName("실패 존재하지 않는 구독")
        @Test
        void fail_subscribe_not_found() {
            // given
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("테스트테스트1",
                    "https://cse.pusan.ac.kr/cse/12412/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Subscribe subscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(subscribe.getId())).willReturn(Optional.empty());

            // when
            Throwable exception = assertThrows(Exception404.class,
                    () -> subscribeWriteService.update(requestDto, subscribe.getId(), user));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 작성자가 아닌 사람이 접근")
        @Test
        void fail_access_data_not_writer() {
            // given
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("테스트테스트1",
                    "https://cse.pusan.ac.kr/cse/12412/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            User anotherUser = dummyEntity.getTestUser("rjsdnxogh@kakao.com", 2L);

            Notice notice = dummyEntity.getTestNoticeList().get(0);
            Subscribe subscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(subscribe.getId())).willReturn(Optional.of(subscribe));

            // when
            Throwable exception = assertThrows(Exception403.class,
                    () -> subscribeWriteService.update(requestDto, subscribe.getId(), anotherUser));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 notice rss 링크 학과 불일치")
        @Test
        void fail_notice_major_not_eqaul_rss_major() {
            // given
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("테스트테스트1",
                    "https://eec.pusan.ac.kr/eec/12412/subview.do");

            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);

            Notice notice = dummyEntity.getTestNoticeList().get(0);
            Subscribe subscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(subscribe.getId())).willReturn(Optional.of(subscribe));

            // when
            Throwable exception = assertThrows(Exception400.class,
                    () -> subscribeWriteService.update(requestDto, subscribe.getId(), user));

            // then
            assertEquals(BaseExceptionStatus.RSS_NOTICE_LINK_MISMATCH.getMessage(), exception.getMessage());
        }
    }

    @DisplayName("구독 상태 변경 테스트")
    @Nested
    class ActiveUpdateTest{
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            Subscribe resultSubscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);
            Subscribe mockSubscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(mockSubscribe.getId())).willReturn(Optional.of(mockSubscribe));

            // when
            SubscribeUpdateResponseDto resultDto = subscribeWriteService.activeUpdate(mockSubscribe.getId(), user);

            // then
            verify(subscribeJpaRepository).saveAndFlush(any());
            assertEquals(!resultSubscribe.getIsActive(), resultDto.isActive());
        }

        @DisplayName("실패 존재하지 않는 구독")
        @Test
        void fail_subscribe_not_found() {
            // given
            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            Subscribe resultSubscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);
            Subscribe mockSubscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(mockSubscribe.getId())).willReturn(Optional.empty());

            // when
            Throwable exception = assertThrows(Exception404.class, () -> subscribeWriteService.activeUpdate(mockSubscribe.getId(), user));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 작성자가 아닌 사람이 접근")
        @Test
        void fail_access_data_not_writer() {
            // given
            User user = dummyEntity.getTestUser("rjsdnxogh12@naver.com", 1L);
            User anotherUser = dummyEntity.getTestUser("rjsdnxogh@kakao.com", 2L);
            Notice notice = dummyEntity.getTestNoticeList().get(0);

            Subscribe resultSubscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);
            Subscribe mockSubscribe = dummyEntity.getTestSubscribeList(user, dummyEntity.getTestNoticeList()).get(0);

            // mock
            given(subscribeJpaRepository.findById(mockSubscribe.getId())).willReturn(Optional.of(mockSubscribe));

            // when
            Throwable exception = assertThrows(Exception403.class, () -> subscribeWriteService.activeUpdate(mockSubscribe.getId(), anotherUser));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getMessage(), exception.getMessage());
        }
    }

}
