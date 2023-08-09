package com.ppap.ppap.service.user;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap.domain.user.Entity.constant.Provider;
import com.ppap.ppap.domain.user.dto.oauth.kakao.KakaoUserInfo;
import com.ppap.ppap.domain.user.utils.kakao.KakaoRestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@DisplayName("카카오 유저 정보 얻기 테스트")
@ExtendWith({MockitoExtension.class})
public class KakaoRestTemplateTest {

    @InjectMocks
    private KakaoRestTemplate kakaoRestTemplate;

    @Mock
    private RestTemplate restTemplate;

    @DisplayName("getKakaoUserInfo 테스트")
    @Nested
    class KakaoInfoTest {

        @DisplayName("성공")
        @Test
        void success() {
            // given
            String token = "testToken";
            String email = "rjsdnxogh@naver.com";
            KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(email);
            ResponseEntity<KakaoUserInfo> fakeResponse = ResponseEntity.ok(kakaoUserInfo);

            // mock
            given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(KakaoUserInfo.class))).willReturn(fakeResponse);

            // when
            KakaoUserInfo result = kakaoRestTemplate.getKakaoUserInfo(token);

            // then
            assertEquals(email, result.email());
            assertEquals(Provider.PROVIDER_KAKAO, result.provider());
        }

        @DisplayName("실패 유효하지않은 토큰")
        @Test
        void fail_invalid_token() {
            // given
            String token = "testToken";
            String email = "rjsdnxogh@naver.com";
            KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(email);
            ResponseEntity<KakaoUserInfo> fakeResponse = ResponseEntity.ok(kakaoUserInfo);

            // mock
            given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(KakaoUserInfo.class))).willThrow(
                    new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "토큰 에러입니다"));

            // when
            Throwable exception = assertThrows(Exception400.class, () -> kakaoRestTemplate.getKakaoUserInfo(token));

            // then
            assertEquals(BaseExceptionStatus.KAKAO_TOKEN_INVALID.getMessage(), exception.getMessage());
        }

        @DisplayName("실패 카카오 연결 에러")
        @Test
        void fail_kakao_connection_error() {
            // given
            String token = "testToken";
            String email = "rjsdnxogh@naver.com";
            KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(email);
            ResponseEntity<KakaoUserInfo> fakeResponse = ResponseEntity.ok(kakaoUserInfo);

            // mock
            given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(KakaoUserInfo.class))).willThrow(
                    new RuntimeException("카카와의 연결이 비정상적으로 종료되었습니다."));

            // when
            Throwable exception = assertThrows(Exception500.class, () -> kakaoRestTemplate.getKakaoUserInfo(token));

            // then
            assertEquals(BaseExceptionStatus.KAKAO_API_CONNECTION_ERROR.getMessage(), exception.getMessage());
        }
    }

    private KakaoUserInfo getKakaoUserInfo(String email) {
        Random random = new Random();
        return new KakaoUserInfo(random.nextLong(), LocalDateTime.now(),
                new KakaoUserInfo.KakaoAccount(true, true, true, true, email));
    }
}
