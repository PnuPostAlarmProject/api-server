package com.ppap.ppap.service.usecase;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.application.usecase.GetScrapSubscirbeUseCase;
import com.ppap.ppap.domain.scrap.dto.ScrapWithSubscribeDto;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GetScrapSubscirbeUseCaseTest {
    @InjectMocks
    private GetScrapSubscirbeUseCase getScrapSubscirbeUseCase;
    @Mock
    private SubscribeReadService subscribeReadService;
    @Mock
    private ScrapReadService scrapReadService;

    private DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("스크랩 화면 유즈케이스 로직 테스트")
    @Nested
    class ScrapViewUseCaseTest {
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
                    .limit(10)
                    .toList();

            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);
            given(scrapReadService.findByUserIdAndNoticeIdFetchJoinContent(testUser.getId(),
                    testSubscribeList.get(0).getNotice().getId(),
                    pageable)).willReturn(testScrapList);

            // when
            ScrapWithSubscribeDto response = getScrapSubscirbeUseCase.execute(
                    Optional.empty(), testUser, pageable);

            // then
            assertEquals(1L, response.curSubscribeId());
            assertEquals(10, response.scraps().size());
            for (int i=1; i<11; i++) {
                assertEquals(true, response.scraps().get(i-1).isScrap());
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
            List<Scrap> testScrapList = testContentList.stream()
                    .map(content -> Scrap.of(testUser, content))
                    .skip(10)
                    .limit(10)
                    .toList();

            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);
            given(scrapReadService.findByUserIdAndNoticeIdFetchJoinContent(testUser.getId(),
                    testSubscribeList.get(0).getNotice().getId(),
                    pageable)).willReturn(testScrapList);

            // when
            ScrapWithSubscribeDto response = getScrapSubscirbeUseCase.execute(
                    Optional.empty(), testUser, pageable);

            // then
            assertEquals(1L, response.curSubscribeId());
            assertEquals(2, response.scraps().size());
            for (int i=0; i<response.scraps().size(); i++) {
                assertEquals(11+i, response.scraps().get(i).contentId());
                assertEquals(true, response.scraps().get(i).isScrap());
            }
        }

        @DisplayName("성공 사용자가 선택한 구독 스크랩")
        @Test
        void success_user_select_subscribe_scrap() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pubDate"));
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Subscribe> testSubscribeList = dummyEntity.getTestSubscribeList(testUser, testNoticeList);
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(1));
            List<Scrap> testScrapList = testContentList.stream()
                    .map(content -> Scrap.of(testUser, content))
                    .limit(10)
                    .toList();

            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);
            given(scrapReadService.findByUserIdAndNoticeIdFetchJoinContent(testUser.getId(),
                    testSubscribeList.get(1).getNotice().getId(),
                    pageable)).willReturn(testScrapList);

            // when
            ScrapWithSubscribeDto response = getScrapSubscirbeUseCase.execute(
                    Optional.of(testSubscribeList.get(1).getId()), testUser, pageable);

            // then
            assertEquals(2L, response.curSubscribeId());
            assertEquals(10, response.scraps().size());
            for (int i=1; i<11; i++) {
                assertEquals(true, response.scraps().get(i-1).isScrap());
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


            // mock
            given(subscribeReadService.getSubscribeEntityList(testUser)).willReturn(testSubscribeList);

            // when
            Throwable exception = assertThrows(Exception404.class, () -> getScrapSubscirbeUseCase.execute(
                    Optional.of(100L), testUser, pageable));

            // then
            assertEquals(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage(), exception.getMessage());
        }
    }
}
