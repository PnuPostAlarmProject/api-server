package com.ppap.ppap.service.subscribe;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.repository.ContentJpaRepository;
import com.ppap.ppap.domain.subscribe.service.ContentWriteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
public class ContentWriteServiceTest {
    @Mock
    private ContentJpaRepository contentJpaRepository;

    @InjectMocks
    private ContentWriteService contentWriteService;

    private DummyEntity dummyEntity = new DummyEntity();

    @DisplayName("contentsSave 테스트")
    @Nested
    class ContentsSaveTest{
        @DisplayName("성공")
        @Test
        void success() {
            // given
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            List<RssData> testRssDataList = dummyEntity.getTestRssList(testNotice);

            // mock
            willDoNothing().given(contentJpaRepository).saveAllBatch(any());

            // when
            contentWriteService.contentsSave(testRssDataList);

            // then
            verify(contentJpaRepository, times(1)).saveAllBatch(any());
        }

        @DisplayName("성공 빈 리스트")
        @Test
        void success_empty_list() {
            // given
            // mock
            // when
            contentWriteService.contentsSave(List.of());

            // then
            verify(contentJpaRepository, times(0)).saveAllBatch(any());
        }

        @DisplayName("실패 디비 쪽 문제")
        @Test
        void fail_save_db_error() {
            // given
            Notice testNotice = dummyEntity.getTestNoticeList().get(0);
            List<RssData> testRssDataList = dummyEntity.getTestRssList(testNotice);

            // mock
            willThrow(new RuntimeException("db error")).given(contentJpaRepository).saveAllBatch(any());

            // when
            Throwable exception = assertThrows(Exception500.class, () -> contentWriteService.contentsSave(testRssDataList));

            // then
            assertEquals(BaseExceptionStatus.CONTENT_SAVE_ERROR.getMessage(), exception.getMessage());
        }
    }
}
