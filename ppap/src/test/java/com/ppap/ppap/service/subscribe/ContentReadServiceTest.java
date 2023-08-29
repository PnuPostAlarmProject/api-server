package com.ppap.ppap.service.subscribe;

import com.ppap.ppap.domain.subscribe.repository.ContentJpaRepository;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("공지사항 내용 Read 서비스 테스트")
@ExtendWith({MockitoExtension.class})
public class ContentReadServiceTest {
    @Mock
    private ContentJpaRepository contentJpaRepository;

    @InjectMocks
    private ContentReadService contentReadService;

    @DisplayName("findDistinctNoticeId 테스트")
    @Nested
    class FindDistinctNoticeIdTest{
        @DisplayName("성공")
        @Test
        void success() {
            // given

            // mock
            // when
            // then
        }
    }
}
