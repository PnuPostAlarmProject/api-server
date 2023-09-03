package com.ppap.ppap.service.scrap;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap.domain.scrap.dto.ScrapFindByContentIdDto;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.repository.ScrapJpaRepository;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ScrapReadServiceTest {
    @Mock
    private ScrapJpaRepository scrapJpaRepository;

    @InjectMocks
    private ScrapReadService scrapReadService;

    private DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("findByContentIds 테스트")
    @Nested
    class FindByContentIdsTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            List<Content> testContentList = dummyEntity.getTestContentList(testNotice);
            List<Long> testContentIds = testContentList.stream()
                    .map(Content::getId)
                    .toList();
            List<Scrap> testScrapList = testContentList.stream()
                    .map(content -> Scrap.of(testUser, content))
                    .toList();

            // mock
            given(scrapJpaRepository.findByContentIds(testUser.getId(), testContentIds)).willReturn(testScrapList);

            // when
            List<ScrapFindByContentIdDto> responseList = scrapReadService.findByContentIds(testUser.getId(), testContentIds);

            // then
            assertEquals(12, responseList.size());
            for (long i=1L; i<=12L; i++) {
                assertEquals(i, responseList.get((int)(i-1)).contentId());
            }
        }
    }

    @DisplayName("findByUserIdAndNoticeIdFetchJoinContent 테스트")
    @Nested
    class FindByUserIdAndNoticeIdFetchJoinContentTest {
        @DisplayName("성공 1페이지")
        @Test
        void success_1page() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            List<Content> testContentList = dummyEntity.getTestContentList(testNotice);
            Pageable pageable = PageRequest.of(0, 10);
            List<Scrap> testScrapList = testContentList.stream()
                    .skip(10L* pageable.getPageNumber())
                    .limit(10)
                    .map(content -> Scrap.of(testUser, content))
                    .toList();

            Page<Scrap> testScrapPage = new PageImpl<>(testScrapList, pageable, testContentList.size());

            // mock
            given(scrapJpaRepository.findByUserIdAndNoticeIdFetchJoinContent(testUser.getId(),
                    testNotice.getId(), pageable)).willReturn(testScrapPage);

            // when
            List<Scrap> responseList = scrapReadService.findByUserIdAndNoticeIdFetchJoinContent(testUser.getId(),
                    testNotice.getId(), pageable);

            // then
            assertEquals(10, responseList.size());
            for (long i=1L; i<=10L; i++) {
                assertEquals(i, responseList.get((int)(i-1)).getContent().getId());
            }

        }

        @DisplayName("성공 2페이지")
        @Test
        void success_2page() {
            // given
            User testUser = dummyEntity.getTestUser("rjsdnxogh@naver.com", 1L);
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            List<Content> testContentList = dummyEntity.getTestContentList(testNotice);
            Pageable pageable = PageRequest.of(1, 10);
            List<Scrap> testScrapList = testContentList.stream()
                    .skip(10L * pageable.getPageNumber())
                    .limit(10)
                    .map(content -> Scrap.of(testUser, content))
                    .toList();

            Page<Scrap> testScrapPage = new PageImpl<>(testScrapList, pageable, testContentList.size());

            // mock
            given(scrapJpaRepository.findByUserIdAndNoticeIdFetchJoinContent(testUser.getId(),
                    testNotice.getId(), pageable)).willReturn(testScrapPage);

            // when
            List<Scrap> responseList = scrapReadService.findByUserIdAndNoticeIdFetchJoinContent(testUser.getId(),
                    testNotice.getId(), pageable);

            // then
            assertEquals(2, responseList.size());
            for (long i=11L; i<=12L; i++) {
                assertEquals(i, responseList.get((int)i-11).getContent().getId());
            }
        }
    }
}
