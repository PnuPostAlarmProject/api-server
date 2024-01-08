package com.ppap.ppap.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import com.ppap.ppap._core.RestDocs;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.utils.UrlFactory;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap.domain.subscribe.dto.SubscribeCreateRequestDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeUpdateRequestDto;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("구독 통합 테스트")
public class SubscribeControllerTest extends RestDocs {


    @MockBean
    private SAXBuilder saxBuilder;
    @MockBean
    private UrlFactory urlFactory;

    @DisplayName("구독 생성 테스트")
    @Transactional
    @Nested
    class CreateTest{
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto(
                    "test123",
                    "https://cse.pusan.ac.kr/bbs/cse/2618/rssList.do",
                    "https://cse.pusan.ac.kr/cse/14669/subview.do"
            );
            String requestBody = om.writeValueAsString(requestDto);

            // mock
            given(urlFactory.getInputStream(anyString(), anyInt(), anyInt())).willReturn(InputStream.nullInputStream());
            given(saxBuilder.build(any(InputStream.class))).willReturn(new Document());

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken));

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response").value("created"),
                    jsonPath("$.error").doesNotExist()
            );
            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(
                            ResourceSnippetParameters.builder()
                                    .description("구독 생성 API")
                                    .requestHeaders(
                                            headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                                    )
                                    .requestFields(
                                            fieldWithPath("title").type(JsonFieldType.STRING).description("구독 제목"),
                                            fieldWithPath("rssLink").type(JsonFieldType.STRING).description("rss 링크"),
                                            fieldWithPath("noticeLink").type(JsonFieldType.STRING).description("공지사항 링크").optional()
                                    )
                                    .build()
                    )
            ));
        }

        @DisplayName("실패 부산대학교가 아닌 rss 링크")
        @Test
        void fail_invalid_rssLink() throws Exception{
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto(
                    "test123",
                    "https://www.mois.go.kr/gpms/view/jsp/rss/rss.jsp?ctxCd=1012",
                    "https://cse.pusan.ac.kr/cse/14669/subview.do"
            );
            String requestBody = om.writeValueAsString(requestDto);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken));

            String response = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.RSS_LINK_NOT_PNU.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.RSS_LINK_NOT_PNU.getStatus())
            );
        }

        @DisplayName("실패 rss가 아닌 링크")
        @Test
        void fail_invalid_rss() throws Exception{
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto(
                    "test123",
                    "https://cse.pusan.ac.kr/bbs/cse/2618/list.do",
                    "https://cse.pusan.ac.kr/cse/14669/subview.do"
            );
            String requestBody = om.writeValueAsString(requestDto);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken));

            String response = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.RSS_LINK_INVALID.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.RSS_LINK_INVALID.getStatus())
            );
        }

        @DisplayName("실패 rss와 공지사항이 같은 학과가 아닌 경우")
        @Test
        void fail_rss_notice_major_mismatch() throws Exception{
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            SubscribeCreateRequestDto requestDto = new SubscribeCreateRequestDto(
                    "test123",
                    "https://cse.pusan.ac.kr/bbs/cse/2615/rsslist.do",
                    "https://eec.pusan.ac.kr/eec/12412/subview.do"
            );
            String requestBody = om.writeValueAsString(requestDto);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken));

            String response = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.RSS_LINK_INVALID.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.RSS_LINK_INVALID.getStatus())
            );
        }
    }

    @DisplayName("구독 조회 테스트")
    @Nested
    class GetListTest{
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    get("/subscribe")
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response[0].subscribeId").value(1),
                    jsonPath("$.response[1].subscribeId").value(2),
                    jsonPath("$.response[2].subscribeId").value(3),
                    jsonPath("$.error").doesNotExist()
            );
            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(
                            ResourceSnippetParameters.builder()
                                    .description("구독 조회 API")
                                    .requestHeaders(
                                            headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                                    )
                                    .build()
                    )
            ));
        }
    }

    @DisplayName("구독 상세 조회 테스트")
    @Nested
    class GetDetailTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 1L;
            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    get("/subscribe/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.subscribeId").value(1),
                    jsonPath("$.response.title").value("테스트1"),
                    jsonPath("$.response.isActive").value("true"),
                    jsonPath("$.error").doesNotExist()
            );
            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(
                            ResourceSnippetParameters.builder()
                                    .description("구독 상세 조회 API")
                                    .requestHeaders(
                                            headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                                    )
                                    .build()
                    )
            ));
        }

        @DisplayName("실패 존재하지 않는 구독")
        @Test
        void fail_subscribe_not_found() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 100L;
            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    get("/subscribe/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getStatus())
            );
        }

        @DisplayName("실패 사용자와 구독 작성자가 일치하지 않음")
        @Test
        void fail_not_writer() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh12@kakao.com");
            Long subscribeId = 1L;
            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    get("/subscribe/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getStatus())
            );
        }
    }

    @DisplayName("구독 수정 테스트")
    @Transactional
    @Nested
    class UpdateTest{
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 1L;
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("modi_test1", "https://cse.pusan.ac.kr/cse/14651/subview.do");
            String requestBody = om.writeValueAsString(requestDto);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/{subscribe_id}", subscribeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("response : " + responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.subscribeId").value(1),
                    jsonPath("$.response.title").value("modi_test1"),
                    jsonPath("$.response.noticeLink").value("https://cse.pusan.ac.kr/cse/14651/subview.do"),
                    jsonPath("$.response.isActive").value("true"),
                    jsonPath("$.error").doesNotExist()
            );

            // document
            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(ResourceSnippetParameters.builder()
                            .description("구독 수정 API")
                            .requestHeaders(
                                    headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                            )
                            .requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("구독 제목"),
                                    fieldWithPath("noticeLink").type(JsonFieldType.STRING).description("공지사항 링크").optional()
                            )
                            .pathParameters(
                                    parameterWithName("subscribe_id").type(SimpleType.INTEGER).description("구독 ID")
                            )
                            .build()
            )));
        }

        @DisplayName("실패 존재하지 않는 구독")
        @Test
        void fail_subscribe_not_exist() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 10L;
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("modi_test1", "https://cse.pusan.ac.kr/cse/14651/subview.do");
            String requestBody = om.writeValueAsString(requestDto);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/{subscribe_id}", subscribeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("response : " + responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getStatus())
            );
        }

        @DisplayName("실패 사용자와 구독 작성자가 일치하지 않음")
        @Test
        void fail_user_writer_mismatch() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh12@kakao.com");
            Long subscribeId = 1L;
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("modi_test1", "https://cse.pusan.ac.kr/cse/14651/subview.do");
            String requestBody = om.writeValueAsString(requestDto);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/{subscribe_id}", subscribeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("response : " + responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getStatus())
            );
        }

        @DisplayName("실패 rss와 공지사항 학과가 일치하지 않음")
        @Test
        void fail_rss_notice_major_mismatch() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 1L;
            SubscribeUpdateRequestDto requestDto = new SubscribeUpdateRequestDto("modi_test1", "https://eec.pusan.ac.kr/eec/14651/subview.do");
            String requestBody = om.writeValueAsString(requestDto);

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/{subscribe_id}", subscribeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .header(JwtProvider.HEADER, accessToken)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("response : " + responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.RSS_NOTICE_LINK_MISMATCH.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.RSS_NOTICE_LINK_MISMATCH.getStatus())
            );
        }
    }

    @DisplayName("구독 상태 변경 테스트")
    @Transactional
    @Nested
    class ActiveUpdateTest{

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 1L;

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/active/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.subscribeId").value(1),
                    jsonPath("$.response.isActive").value("false"),
                    jsonPath("$.error").doesNotExist()
            );

            // document
            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(ResourceSnippetParameters.builder()
                            .description("구독 상태 변경 API")
                            .requestHeaders(
                                    headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                            )
                            .pathParameters(
                                    parameterWithName("subscribe_id").type(SimpleType.INTEGER).description("구독 ID")
                            )
                            .build()
                    )));
        }

        @DisplayName("실패 존재하지 않은 구독")
        @Test
        void fail_subscribe_not_exist() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 10L;

            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/active/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getStatus())
            );
        }

        @DisplayName("실패 사용자와 구독 작성자가 일치하지 않음")
        @Test
        void fail_not_writer() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh12@kakao.com");
            Long subscribeId = 1L;
            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/active/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getStatus())
            );
        }
    }

    @DisplayName("구독 삭제 테스트")
    @Nested
    class SubscribeDeleteTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 1L;
            // mock

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/delete/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error").doesNotExist()
            );

            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(ResourceSnippetParameters.builder()
                            .description("구독 삭제 API")
                            .requestHeaders(
                                    headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                            )
                            .pathParameters(
                                    parameterWithName("subscribe_id").type(SimpleType.INTEGER).description("구독 ID")
                            )
                            .build()
                    )
            ));
        }

        @DisplayName("실패 존재하지 않는 구독")
        @Test
        void fail_subscribe_not_exist() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 4L;

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/delete/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getStatus())
            );
        }

        @DisplayName("실패 작성자가 아닌 유저의 접근")
        @Test
        void fail_not_writer() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh12@kakao.com");
            Long subscribeId = 1L;

            // when
            ResultActions resultActions = mvc.perform(
                    post("/subscribe/delete/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN.getStatus())
            );
        }
    }
}
