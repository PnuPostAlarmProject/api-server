package com.ppap.ppap.service.scrap;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception403;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.repository.ScrapJpaRepository;
import com.ppap.ppap.domain.scrap.service.ScrapWriteService;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import com.ppap.ppap.domain.user.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScrapWriteServiceTest {
    @Mock
    private ScrapJpaRepository scrapJpaRepository;
    @Mock
    private ContentReadService contentReadService;

    @InjectMocks
    private ScrapWriteService scrapWriteService;

    private DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("create 테스트")
    @Nested
    class CreateTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            Content testContent = dummyEntity.getTestContentList(testNotice).get(0);
            ArgumentCaptor<Scrap> scrapArgumentCaptor = ArgumentCaptor.forClass(Scrap.class);
            Scrap scrap = Scrap.of(testUser, testContent);
            // mock
            given(contentReadService.findById(testContent.getId())).willReturn(testContent);
            given(scrapJpaRepository.existsByUserIdAndContentId(testUser.getId(), testContent.getId()))
                    .willReturn(false);

            // when
            scrapWriteService.create(testUser, testContent.getId());

            // then
            verify(scrapJpaRepository, times(1)).save(scrapArgumentCaptor.capture());
            assertEquals(scrap, scrapArgumentCaptor.getValue());
        }

        @DisplayName("실패 이미 존재하는 스크랩")
        @Test
        void fail_already_exist_scrap() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            Content testContent = dummyEntity.getTestContentList(testNotice).get(0);

            // mock
            given(contentReadService.findById(testContent.getId())).willReturn(testContent);
            given(scrapJpaRepository.existsByUserIdAndContentId(testUser.getId(), testContent.getId()))
                    .willReturn(true);

            // when
            Throwable exception = assertThrows(Exception400.class,
                    () -> scrapWriteService.create(testUser, testContent.getId()));

            // then
            assertEquals(BaseExceptionStatus.SCRAP_ALREADY_EXIST.getMessage(),exception.getMessage());
        }
    }

    @DisplayName("deleteByScrapId 테스트")
    @Nested
    class DeleteByScrapIdTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            Content testContent = dummyEntity.getTestContentList(testNotice).get(0);
            Scrap scrap = Scrap.of(testUser, testContent);

            // mock
            given(scrapJpaRepository.findById(scrap.getId())).willReturn(Optional.of(scrap));

            // when
            scrapWriteService.deleteByScrapId(testUser, scrap.getId());

            // then
            verify(scrapJpaRepository, times(1)).delete(scrap);
        }

        @DisplayName("실패 존재하지 않는 스크랩")
        @Test
        void fail_scrap_not_found() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            Content testContent = dummyEntity.getTestContentList(testNotice).get(0);
            Scrap scrap = Scrap.of(testUser, testContent);

            // mock
            given(scrapJpaRepository.findById(scrap.getId())).willReturn(Optional.empty());

            // when
            Throwable exception = assertThrows(Exception404.class,
                    () -> scrapWriteService.deleteByScrapId(testUser, scrap.getId()));

            // then
            assertEquals(BaseExceptionStatus.SCRAP_NOT_FOUND.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 작성자가 아닌 사람의 요청")
        @Test
        void fail_request_not_writer() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            User anotherUser = dummyEntity.getTestUser("rjsdnxogh12@gmail.com", 2L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            Content testContent = dummyEntity.getTestContentList(testNotice).get(0);
            Scrap scrap = Scrap.of(testUser, testContent);

            // mock
            given(scrapJpaRepository.findById(scrap.getId())).willReturn(Optional.of(scrap));

            // when
            Throwable exception = assertThrows(Exception403.class,
                    () -> scrapWriteService.deleteByScrapId(anotherUser, scrap.getId()));

            // then
            assertEquals(BaseExceptionStatus.SCRAP_FORBIDDEN.getMessage(), exception.getMessage());
        }
    }

    @DisplayName("deleteByContentId 테스트")
    @Nested
    class DeleteByContentIdTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            Content testContent = dummyEntity.getTestContentList(testNotice).get(0);
            Scrap scrap = Scrap.of(testUser, testContent);

            // mock
            given(scrapJpaRepository.findByContentIdAndUserId(testUser.getId(), testContent.getId()))
                    .willReturn(Optional.of(scrap));

            // when
            scrapWriteService.deleteByContentId(testUser, testContent.getId());

            // then
            verify(scrapJpaRepository, times(1)).delete(scrap);
        }

        @DisplayName("실패 존재하지 않는 스크랩")
        @Test
        void fail_scrap_not_found() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            Content testContent = dummyEntity.getTestContentList(testNotice).get(0);
            Scrap scrap = Scrap.of(testUser, testContent);

            // mock
            given(scrapJpaRepository.findByContentIdAndUserId(testUser.getId(), testContent.getId()))
                    .willReturn(Optional.empty());

            // when
            Throwable exception = assertThrows(Exception404.class,
                    () -> scrapWriteService.deleteByContentId(testUser, testContent.getId()));

            // then
            assertEquals(BaseExceptionStatus.SCRAP_NOT_FOUND.getMessage(), exception.getMessage());
        }
    }
}
