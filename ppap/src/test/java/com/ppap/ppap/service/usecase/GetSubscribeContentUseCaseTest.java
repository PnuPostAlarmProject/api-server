package com.ppap.ppap.service.usecase;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.application.usecase.GetSubscribeContentUseCase;
import com.ppap.ppap.domain.scrap.dto.ScrapFindByContentIdDto;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.subscribe.dto.SubscribeWithContentScrapDto;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetSubscribeContentUseCaseTest {
    @InjectMocks
    private GetSubscribeContentUseCase getSubscribeContentUseCase;
    @Mock
    private SubscribeReadService subscribeReadService;
    @Mock
    private ContentReadService contentReadService;
    @Mock
    private ScrapReadService scrapReadService;

    private DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("컨텐츠 화면 유즈케이스 로직 테스트")
    @Nested
    class ContentViewUseCaseTest {

        @DisplayName("성공 기본 페이지")
        @Test
        void success_default_page() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pubDate"));
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Subscribe> testSubscribeList = dummyEntity.getTestSubscribeList(testUser, testNoticeList);
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));
            List<Scrap> testScrapList = testContentList.stream()
                    .map(content -> Scrap.of(testUser, content))
                    .limit(5)
                    .toList();
            ArgumentCaptor<Long> curSubscribeId = ArgumentCaptor.forClass(Long.class);

            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);
            given(contentReadService.findByNoticeId(testSubscribeList.get(0).getNotice().getId(), pageable))
                    .willReturn(testContentList.stream().limit(10).toList());
            given(scrapReadService.findByContentIds(testUser.getId(),
                    testContentList.stream().limit(10).map(Content::getId).toList()))
                    .willReturn(testScrapList.stream().map(ScrapFindByContentIdDto::of).toList());

            // when
            SubscribeWithContentScrapDto response = getSubscribeContentUseCase.execute(Optional.empty(),
                    testUser, pageable);

            // then
            verify(contentReadService, times(1)).findByNoticeId(curSubscribeId.capture(), any());
            assertEquals(curSubscribeId.getValue(), 1L);
            assertEquals(10, response.contents().size());
            for (int i=0; i<5; i++) {
                assertEquals(true, response.contents().get(i).isScraped());
            }
            for (int i=5; i<10; i++) {
                assertEquals(false, response.contents().get(i).isScraped());
            }
        }

        @DisplayName("성공 기본 2페이지")
        @Test
        void success_default_2page() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "pubDate"));
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Subscribe> testSubscribeList = dummyEntity.getTestSubscribeList(testUser, testNoticeList);
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));
            List<Scrap> testScrapList = List.of();
            ArgumentCaptor<Long> curSubscribeId = ArgumentCaptor.forClass(Long.class);

            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);
            given(contentReadService.findByNoticeId(testSubscribeList.get(0).getNotice().getId(), pageable))
                    .willReturn(testContentList.stream().skip(10).limit(10).toList());
            given(scrapReadService.findByContentIds(testUser.getId(),
                    testContentList.stream().skip(10).limit(10).map(Content::getId).toList()))
                    .willReturn(List.of());

            // when
            SubscribeWithContentScrapDto response = getSubscribeContentUseCase.execute(Optional.empty(),
                    testUser, pageable);

            // then
            verify(contentReadService, times(1)).findByNoticeId(curSubscribeId.capture(), any());
            assertEquals(curSubscribeId.getValue(), 1L);
            assertEquals(2, response.contents().size());
            for (int i=0; i<response.contents().size(); i++) {
                assertEquals(false, response.contents().get(i).isScraped());
            }
        }

        @DisplayName("성공 구독 선택 페이지")
        @Test
        void success_select_subscribe_page() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pubDate"));
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Subscribe> testSubscribeList = dummyEntity.getTestSubscribeList(testUser, testNoticeList);
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));
            List<Scrap> testScrapList = List.of();
            ArgumentCaptor<Long> curSubscribeId = ArgumentCaptor.forClass(Long.class);

            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);
            given(contentReadService.findByNoticeId(testSubscribeList.get(1).getNotice().getId(), pageable))
                    .willReturn(testContentList.stream().limit(10).toList());
            given(scrapReadService.findByContentIds(testUser.getId(),
                    testContentList.stream().limit(10).map(Content::getId).toList()))
                    .willReturn(List.of());

            // when
            SubscribeWithContentScrapDto response = getSubscribeContentUseCase.execute(Optional.of(2L),
                    testUser, pageable);

            // then
            verify(contentReadService, times(1)).findByNoticeId(curSubscribeId.capture(), any());
            assertEquals(curSubscribeId.getValue(), 2L);
            assertEquals(10, response.contents().size());
            for (int i=0; i<response.contents().size(); i++) {
                assertEquals(false, response.contents().get(i).isScraped());
            }
        }

        @DisplayName("실패 존재하지 않는 구독")
        @Test
        void fail_subscribe_not_found() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pubDate"));
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Subscribe> testSubscribeList = dummyEntity.getTestSubscribeList(testUser, testNoticeList);
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));
            List<Scrap> testScrapList = List.of();
            ArgumentCaptor<Long> curSubscribeId = ArgumentCaptor.forClass(Long.class);

            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);

            // when
            Throwable exception = assertThrows(Exception404.class,
                    () -> getSubscribeContentUseCase.execute(Optional.of(100L), testUser, pageable));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage(), exception.getMessage() );
        }
    }
}
