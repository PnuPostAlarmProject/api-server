package com.ppap.ppap.service.subscribe;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.repository.ContentJpaRepository;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DisplayName("공지사항 내용 Read 서비스 테스트")
@ExtendWith({MockitoExtension.class})
public class ContentReadServiceTest {
    @Mock
    private ContentJpaRepository contentJpaRepository;

    @InjectMocks
    private ContentReadService contentReadService;

    private DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("findDistinctNoticeId 테스트")
    @Nested
    class FindDistinctNoticeIdTest{
        @DisplayName("성공")
        @Test
        void success() {
            // given
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));
            Set<Long> contentDistinctIds = testContentList.stream()
                    .map(Content::getId)
                    .collect(Collectors.toSet());

            // mock
            given(contentJpaRepository.findDistinctNoticeIdIn(testNoticeList)).willReturn(contentDistinctIds);

            // when
            Set<Long> results = contentReadService.findDistinctNoticeIdIn(testNoticeList);

            // then
            assertEquals(12, results.size());
            for (long i = 1L; i<13L; i++) {
                assertTrue(results.contains(i));
            }
        }
    }

    @DisplayName("findByNoticeId 테스트")
    @Nested
    class FindByNoticeIdTest {
        @DisplayName("성공 1페이지")
        @Test
        void success_1page() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));
            Page<Content> testContentPage;
            if(testContentList.size() >= pageable.getPageSize()) {
                testContentPage = new PageImpl<>(testContentList.subList(0, 10) ,pageable, testContentList.size());
            }else {
                testContentPage = new PageImpl<>(testContentList.subList(0, testContentList.size()) ,pageable, testContentList.size());
            }

            // mock
            given(contentJpaRepository.findByNoticeId(testNoticeList.get(0).getId(),pageable)).willReturn(testContentPage);

            // when
            List<Content> result = contentReadService.findByNoticeId(testNoticeList.get(0).getId(), pageable);

            // then
            assertEquals(10, result.size());
        }

        @DisplayName("성공 2페이지")
        @Test
        void success_2page() {
            // given
            Pageable pageable = PageRequest.of(1, 10);
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));
            Page<Content> testContentPage;
            if (testContentList.size() >= 20) {
                testContentPage = new PageImpl<>(testContentList.subList(10, 20), pageable, testContentList.size());
            }else if( 10<= testContentList.size()){
                testContentPage = new PageImpl<>(testContentList.subList(10, testContentList.size()), pageable, testContentList.size());
            } else{
                testContentPage = new PageImpl<>(List.of(), pageable, testContentList.size());
            }

            // mock
            given(contentJpaRepository.findByNoticeId(testNoticeList.get(0).getId(),pageable)).willReturn(testContentPage);

            // when
            List<Content> result = contentReadService.findByNoticeId(testNoticeList.get(0).getId(), pageable);

            // then
            assertEquals(2, result.size());
        }
    }

    @DisplayName("findById 테스트")
    @Nested
    class FindByIdTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));

            // mock
            given(contentJpaRepository.findById(testContentList.get(0).getId())).willReturn(Optional.of(
				testContentList.get(0)));

            // when
            Content content = contentReadService.findById(testContentList.get(0).getId());

            // then
            assertEquals(1L, content.getId());
        }

        @DisplayName("실패 없는 공지사항 내용")
        @Test
        void fail_content_not_exist() {
            // given
            List<Notice> testNoticeList = dummyEntity.getTestNoticeList();
            List<Content> testContentList = dummyEntity.getTestContentList(testNoticeList.get(0));

            // mock
            given(contentJpaRepository.findById(testContentList.get(0).getId())).willReturn(Optional.empty());

            // when
            Throwable throwable = assertThrows(Exception404.class, () -> contentReadService.findById(
				testContentList.get(0).getId()));

            // then
            assertEquals(BaseExceptionStatus.CONTENT_NOT_FOUND.getMessage(), throwable.getMessage());
        }
    }
}
