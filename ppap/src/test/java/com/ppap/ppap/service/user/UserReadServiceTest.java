package com.ppap.ppap.service.user;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap.domain.redis.repository.RefreshTokenRepository;
import com.ppap.ppap.domain.redis.service.RefreshTokenService;
import com.ppap.ppap.domain.user.Entity.User;
import com.ppap.ppap.domain.user.Entity.constant.Role;
import com.ppap.ppap.domain.user.UserRepository;
import com.ppap.ppap.domain.user.dto.ReissueDto;
import com.ppap.ppap.domain.user.service.UserReadService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@DisplayName("유저 Read 서비스 테스트")
@ExtendWith({MockitoExtension.class})
public class UserReadServiceTest {

    @InjectMocks
    private UserReadService userReadService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        JwtProvider.ACCESS_SECRET = UUID.randomUUID().toString();
        JwtProvider.REFRESH_SECRET = UUID.randomUUID().toString();
        JwtProvider.EXP = 1000L * 60 * 30; // 30분
        JwtProvider.REFRESH_EXP = 1000L * 3600 * 24 * 365 * 10; // 10년
    }

    @DisplayName("reissue 테스트")
    @Nested
    class ReissueTest{
        @DisplayName("성공")
        @Test
        void success() {
            // given
            User user = getUser();
            String refreshToken = JwtProvider.createRefreshToken(user);
            ReissueDto.ReissueRequestDto requestDto = new ReissueDto.ReissueRequestDto(refreshToken);

            // mock
            given(refreshTokenService.existsById(refreshToken)).willReturn(true);
            willDoNothing().given(refreshTokenService).deleteById(any());
            willDoNothing().given(refreshTokenService).save(any(), any(), any());

            // when
            ReissueDto.ReissueResponseDto responseDto = userReadService.reissue(requestDto);

            // then
            assertTrue(responseDto.accessToken().startsWith("Bearer "));
            assertTrue(responseDto.refreshToken().length() != 0);
        }

        @DisplayName("실패 만료된 토큰")
        @Test
        void fail_expired_token() {
            // given
            JwtProvider.REFRESH_EXP = -1L;
            User user = getUser();
            String refreshToken = JwtProvider.createRefreshToken(user);
            ReissueDto.ReissueRequestDto requestDto = new ReissueDto.ReissueRequestDto(refreshToken);

            // mock

            // when
            Throwable exception = assertThrows(Exception400.class, () -> userReadService.reissue(requestDto)) ;

            // then
            assertEquals(BaseExceptionStatus.REFRESH_TOKEN_EXPIRED.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 유효하지 않은 토큰")
        @Test
        void fail_not_invalid_token() {
            // given
            String refreshToken = "sdfjwkfajld";
            ReissueDto.ReissueRequestDto requestDto = new ReissueDto.ReissueRequestDto(refreshToken);

            // mock

            // when
            Throwable exception = assertThrows(Exception400.class, () -> userReadService.reissue(requestDto)) ;

            // then
            assertEquals(BaseExceptionStatus.REFRESH_TOKEN_INVALID.getMessage(), exception.getMessage());
        }
    }

    @DisplayName("로그아웃 테스트")
    @Nested
    class LogoutTest{

        @DisplayName("성공")
        @Test
        void success() {
            // given
            User user = getUser();
            String accessToken = JwtProvider.create(user);

            // mock
            willDoNothing().given(refreshTokenService).deleteByAccessToken(accessToken);

            // when
            userReadService.logout(accessToken);

            // then
            verify(refreshTokenService).deleteByAccessToken(accessToken);
        }
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .email("rjsdnxogh@naver.com")
                .role(Role.ROLE_USER)
                .build();
    }
}
