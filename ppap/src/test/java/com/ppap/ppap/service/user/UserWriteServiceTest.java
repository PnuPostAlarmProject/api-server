package com.ppap.ppap.service.user;

import com.ppap.ppap._core.DummyEntity;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap.domain.redis.service.BlackListTokenService;
import com.ppap.ppap.domain.redis.service.RefreshTokenService;
import com.ppap.ppap.domain.user.dto.FcmTokenDto;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.repository.UserJpaRepository;
import com.ppap.ppap.domain.user.dto.LoginMemberResponseDto;
import com.ppap.ppap.domain.user.dto.oauth.kakao.KakaoUserInfo;
import com.ppap.ppap.domain.user.mapper.UserMapper;
import com.ppap.ppap.domain.user.service.DeviceWriteService;
import com.ppap.ppap.domain.user.service.UserWriteService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("유저 Write 서비스 테스트")
@ExtendWith({MockitoExtension.class})
public class UserWriteServiceTest {
    @InjectMocks
    private UserWriteService userWriteService;
    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private DeviceWriteService deviceWriteService;
    @Spy
    private UserMapper userMapper = new UserMapper(new BCryptPasswordEncoder());

    private final DummyEntity dummyEntity = new DummyEntity();

    @BeforeEach
    public void setUp() {
        JwtProvider.ACCESS_SECRET = UUID.randomUUID().toString();
        JwtProvider.REFRESH_SECRET = UUID.randomUUID().toString();
        JwtProvider.EXP = 1000L * 60 * 30; // 30분
        JwtProvider.REFRESH_EXP = 1000L * 3600 * 24 * 365 * 10; // 10년
    }

    @DisplayName("카카오 로그인 테스트")
    @Nested
    class KakaoLogin {
        @DisplayName("성공 회원가입")
        @Test
        void success_signup() {
            // given
            String email = "rjsdnxogh@naver.com";
            KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(email);
            User user = userMapper.userInfoToUser(kakaoUserInfo);
            FcmTokenDto fcmTokenDto = new FcmTokenDto("testToken");

            // mock
            given(userJpaRepository.findByEmail(email)).willReturn(Optional.empty());
            given(userJpaRepository.save(user)).willReturn(user);
            willDoNothing().given(refreshTokenService).save(any(), any(), any());

            // when
            LoginMemberResponseDto responseDto = userWriteService.socialLogin(kakaoUserInfo, fcmTokenDto);

            // then
            Assertions.assertTrue(responseDto.accessToken().startsWith("Bearer "));
            Assertions.assertTrue(responseDto.refreshToken().length() != 0);
        }

        @DisplayName("성공 로그인")
        @Test
        void success_login() {
            // given
            String email = "rjsdnxogh@naver.com";
            KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(email);
            User user = userMapper.userInfoToUser(kakaoUserInfo);
            FcmTokenDto fcmTokenDto = new FcmTokenDto("testToken");

            // mock
            given(userJpaRepository.findByEmail(email)).willReturn(Optional.of(user));
            willDoNothing().given(refreshTokenService).save(any(), any(), any());

            // when
            LoginMemberResponseDto responseDto = userWriteService.socialLogin(kakaoUserInfo, fcmTokenDto);

            // then
            Assertions.assertTrue(responseDto.accessToken().startsWith("Bearer "));
            Assertions.assertTrue(responseDto.refreshToken().length() != 0);
        }
    }

    @DisplayName("회원 탈퇴 테스트")
    @Nested
    class UserDelete{
        @DisplayName("성공")
        @Test
        void success() {
            // given
            String email = "rjsdnxogh@naver.com";
            User user = dummyEntity.getTestUser(email, 1L);
            String accessToken = "testAccessToken";

            // mock
            given(userJpaRepository.findById(user.getId())).willReturn(Optional.of(user));
            willDoNothing().given(userJpaRepository).delete(user);
            willDoNothing().given(refreshTokenService).deleteByAccessToken(accessToken);

            // when
            userWriteService.delete(user, accessToken);

            // then
            verify(userJpaRepository).delete(user);
        }

        @DisplayName("실패 존재하지 않는 회원")
        @Test
        void fail_user_not_exist() {
            // given
            String email = "rjsdnxogh@naver.com";
            User user = dummyEntity.getTestUser(email, 1L);
            String accessToken = "testAccessToken";

            // mock
            given(userJpaRepository.findById(user.getId())).willReturn(Optional.empty());

            // when
            Throwable throwable = assertThrows(Exception404.class,
                () -> userWriteService.delete(user, accessToken));

            // then
            assertEquals(BaseExceptionStatus.USER_NOT_FOUND.getMessage(), throwable.getMessage());
        }
    }


    private KakaoUserInfo getKakaoUserInfo(String email) {
        Random random = new Random();
        return new KakaoUserInfo(random.nextLong(), LocalDateTime.now(),
                new KakaoUserInfo.KakaoAccount(true, true, true, true, email));
    }
}
