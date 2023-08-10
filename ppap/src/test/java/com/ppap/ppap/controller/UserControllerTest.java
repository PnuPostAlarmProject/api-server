package com.ppap.ppap.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ppap.ppap._core.RestDocs;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.domain.redis.entity.RefreshToken;
import com.ppap.ppap.domain.redis.repository.RefreshTokenRepository;
import com.ppap.ppap.domain.redis.service.RefreshTokenService;
import com.ppap.ppap.domain.user.Entity.User;
import com.ppap.ppap.domain.user.dto.LoginMemberResponseDto;
import com.ppap.ppap.domain.user.dto.ReissueDto;
import com.ppap.ppap.domain.user.dto.oauth.kakao.KakaoUserInfo;
import com.ppap.ppap.domain.user.utils.kakao.KakaoRestTemplate;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.*;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("유저 통합 테스트")
public class UserControllerTest extends RestDocs {

    // 외부 연결 요소들은 Mock 처리
    @SpyBean
    private KakaoRestTemplate kakaoRestTemplate;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    private final String KAKAO_URL = "https://kapi.kakao.com/v2/user/me?property_keys=[\"kakao_account.email\"]";

    @Nested
    @DisplayName("유저 카카오 로그인 테스트")
    class UserLogin {
        @DisplayName("유저 카카오 로그인 테스트 성공")
        @Transactional
        @Test
        void user_kakao_login_test() throws Exception {
            // given
            String token = "testToken";
            String email = "rjsdnxogh@naver.com";
            KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(email);
            ResponseEntity<KakaoUserInfo> fakeResponse = ResponseEntity.ok(kakaoUserInfo);


            // mock
            // 정확하게 조건을 넣고 싶다면 eq()를 사용해야한다.
            given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(KakaoUserInfo.class))).willReturn(fakeResponse);

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/kakao/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Kakao", token)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            ApiUtils.ApiResult<LoginMemberResponseDto> resultDto = om.readValue(responseBody, new TypeReference<ApiUtils.ApiResult<LoginMemberResponseDto>>() {});


            // then
            assertTrue(resultDto.response().accessToken().startsWith("Bearer "));
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.accessToken").exists(),
                    jsonPath("$.response.refreshToken").exists(),
                    jsonPath("$.error").doesNotExist()
            );

            resultActions.andDo(
                    document(
                            snippt,
                            getDocumentRequest(),
                            getDocumentResponse(),
                            resource(
                                    ResourceSnippetParameters.builder()
//                                            .tag("유저")
                                            .description("카카오 로그인 API")
                                            .requestHeaders(
                                            headerWithName("Kakao").description("카카오 Access토큰: 카카오 액세스 토큰을 넣어야 합니다.(그대로 입력시 에러발생)")
                                            )
                                            .build()
                            )
                    )
            );
        }

        @DisplayName("유저 카카오 로그인 테스트 실패 카카오 액세스 토큰 없음")
        @Test
        void user_kakao_login_test_fail_kakao_access_token_missing() throws Exception {
            // given

            // mock
            given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(KakaoUserInfo.class))).willThrow(
                    new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "kakao token error")
            );

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/kakao/login")
                            .contentType(MediaType.APPLICATION_JSON)

            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            ApiUtils.ApiResult<LoginMemberResponseDto> resultDto = om.readValue(responseBody, new TypeReference<ApiUtils.ApiResult<LoginMemberResponseDto>>() {
            });


            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.KAKAO_TOKEN_MISSING.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.KAKAO_TOKEN_MISSING.getStatus())
            );
        }

        @DisplayName("유저 카카오 로그인 테스트 실패 유효하지 않은 카카오 액세스 토큰 ")
        @Test
        void user_kakao_login_test_fail_invalid_kakao_access_token() throws Exception {
            // given
            String token = "testToken";

            // mock
            given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(KakaoUserInfo.class))).willThrow(
                    new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "kakao token error")
            );

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/kakao/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Kakao", token)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            ApiUtils.ApiResult<LoginMemberResponseDto> resultDto = om.readValue(responseBody, new TypeReference<ApiUtils.ApiResult<LoginMemberResponseDto>>() {
            });


            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.KAKAO_TOKEN_INVALID.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.KAKAO_TOKEN_INVALID.getStatus())
            );
        }
    }

    @DisplayName("토큰 재발행 테스트")
    @Nested
    class Reissue {
        @DisplayName("토큰 재발행 테스트 성공")
        @Test
        public void reissue_test() throws Exception {
            // given
            String email = "rjsdnxogh@naver.com";
            User user = getUser(email);
            String refreshToken = JwtProvider.createRefreshToken(user);
            ReissueDto.ReissueRequestDto testDto = new ReissueDto.ReissueRequestDto(refreshToken);
            String requestBody = om.writeValueAsString(testDto);

            // mock
            given(refreshTokenRepository.existsById(refreshToken)).willReturn(true);
            willDoNothing().given(refreshTokenRepository).deleteById(refreshToken);

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            ApiUtils.ApiResult<ReissueDto.ReissueResponseDto> response = om.readValue(responseBody,
                    new TypeReference<ApiUtils.ApiResult<ReissueDto.ReissueResponseDto>>() {});

            // then
            assertTrue(response.response().accessToken().startsWith("Bearer "));
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.accessToken").isString(),
                    jsonPath("$.response.refreshToken").isString(),
                    jsonPath("$.error").doesNotExist()
            );

            resultActions.andDo(
                    document(
                            snippt,
                            getDocumentRequest(),
                            getDocumentResponse(),
                            resource(
                                    ResourceSnippetParameters.builder()
//                                            .tag("유저")
                                            .description("토큰 재발행 API")
                                            .requestFields(
                                                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("Refresh 토큰")
                                            )
                                            .build()
                            )
                    )
            );
        }

        @DisplayName("토큰 재발행 테스트 실패 토큰 없음")
        @Test
        public void reissue_test_fail_no_token() throws Exception {
            // given
            ReissueDto.ReissueRequestDto testDto = new ReissueDto.ReissueRequestDto(null);
            String requestBody = om.writeValueAsString(testDto);
            // mock
            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            );

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.status").value(400)
            );

        }

        @DisplayName("토큰 재발행 테스트 실패 유효하지 않은 토큰")
        @Test
        public void reissue_test_fail_invalid_token() throws Exception {
            // given
            String refreshToken = "testToken";
            ReissueDto.ReissueRequestDto testDto = new ReissueDto.ReissueRequestDto(refreshToken);
            String requestBody = om.writeValueAsString(testDto);

            // mock


            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            );
            System.out.println(resultActions.andReturn().getResponse().getContentAsString());

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.REFRESH_TOKEN_INVALID.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.REFRESH_TOKEN_INVALID.getStatus())
            );
        }

        @DisplayName("토큰 재발행 테스트 실패 레디스에 없는 토큰")
        @Test
        public void reissue_test_fail_token_not_found_in_redis() throws Exception {
            // given
            String email = "rjsdnxogh@naver.com";
            User user = getUser(email);
            String refreshToken = JwtProvider.createRefreshToken(user);
            ReissueDto.ReissueRequestDto testDto = new ReissueDto.ReissueRequestDto(refreshToken);
            String requestBody = om.writeValueAsString(testDto);

            // mock
            given(refreshTokenRepository.existsById(refreshToken)).willReturn(false);

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            );
            System.out.println(resultActions.andReturn().getResponse().getContentAsString());

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.REFRESH_TOKEN_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.REFRESH_TOKEN_NOT_FOUND.getStatus())
            );
        }
    }

    @DisplayName("로그아웃 테스트")
    @Nested
    class Logout{

        @DisplayName("로그아웃 테스트 성공")
        @Test
        void logout_test_success() throws Exception {
            // given
            String email = "rjsdnxogh@naver.com";
            User user = getUser(email);
            String accessToken = JwtProvider.create(user);
            String refreshToken = JwtProvider.createRefreshToken(user);
            RefreshToken refreshTokenEntity = RefreshToken.of(refreshToken, accessToken, user);

            // mock
            given(refreshTokenRepository.findByAccessToken(accessToken)).willReturn(Optional.of(refreshTokenEntity));
            willDoNothing().given(refreshTokenRepository).deleteById(accessToken);

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error").doesNotExist()
            );

            resultActions.andDo(document(
                    snippt,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(
                            ResourceSnippetParameters.builder()
                                    .description("로그아웃 API")
                                    .requestHeaders(
                                            headerWithName(JwtProvider.HEADER).description("Access JWT 토큰")
                                    )
                                    .build()
                    )
            ));
        }

        @DisplayName("로그아웃 테스트 실패 유효하지 않은 토큰")
        @Test
        void logout_test_fail_not_invalid_token() throws Exception {
            // given
            String email = "rjsdnxogh@naver.com";
            String accessToken = getAccessToken(email);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
//                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value("인증되지 않은 사용자입니다."),
                    jsonPath("$.error.status").value(401)
            );

        }

        @DisplayName("로그아웃 테스트 실패 레디스에 토큰 없음")
        @Test
        void logout_test_fail_token_not_found_in_redis() throws Exception {
            // given
            String email = "rjsdnxogh@naver.com";
            String accessToken = getAccessToken(email);

            // mock
            given(refreshTokenRepository.findByAccessToken(accessToken)).willReturn(Optional.empty());

            // when
            ResultActions resultActions = mvc.perform(
                    post("/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.REFRESH_TOKEN_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.REFRESH_TOKEN_NOT_FOUND.getStatus())
            );

        }
    }

    private KakaoUserInfo getKakaoUserInfo(String email) {
        Random random = new Random();
        return new KakaoUserInfo(random.nextLong(), LocalDateTime.now(),
                new KakaoUserInfo.KakaoAccount(true, true, true, true, email));
    }
}
