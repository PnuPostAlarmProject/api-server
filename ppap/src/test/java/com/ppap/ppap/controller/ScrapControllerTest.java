package com.ppap.ppap.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.ppap.ppap._core.RestDocs;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ScrapControllerTest extends RestDocs {

    @DisplayName("스크랩 저장하기 통합 테스트")
    @Nested
    class CreateScrapControllerTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long contentId = 4L;

            // when
            ResultActions resultActions = mvc.perform(
                    post("/api/v0/scrap/create/{content_id}", contentId)
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
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(ResourceSnippetParameters.builder()
                            .description("스크랩 생성 API")
                            .requestHeaders(
                                    headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                            )
                            .pathParameters(
                                    parameterWithName("content_id").type(SimpleType.INTEGER).description("공지사항 내용 ID")
                            )
                                .build()
                    )
            ));
        }
        @DisplayName("실패 존재하지 않는 공지사항 내용")
        @Test
        void fail_content_not_found() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long contentId = 100L;

            // when
            ResultActions resultActions = mvc.perform(
                    post("/api/v0/scrap/create/{content_id}", contentId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.CONTENT_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.CONTENT_NOT_FOUND.getStatus())
            );
        }

        @DisplayName("실패 이미 존재하는 스크랩")
        @Test
        void fail_already_exist_scrap() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long contentId = 1L;

            // when
            ResultActions resultActions = mvc.perform(
                    post("/api/v0/scrap/create/{content_id}", contentId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SCRAP_ALREADY_EXIST.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SCRAP_ALREADY_EXIST.getStatus())
            );
        }
    }

    @DisplayName("ContentId로 스크랩 삭제 컨트롤러 테스트")
    @Nested
    class DeleteScrapByContentIdControllerTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long contentId = 1L;

            // when
            ResultActions resultActions = mvc.perform(
                    post("/api/v0/scrap/delete/content/{content_id}", contentId)
                            .header(JwtProvider.HEADER, accessToken)
            );

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
                            .description("스크랩 삭제 API")
                            .requestHeaders(
                                    headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                            )
                            .pathParameters(
                                    parameterWithName("content_id").type(SimpleType.INTEGER).description("공지사항 내용 ID")
                            )
                            .build())
            ));
        }

        @DisplayName("실패 존재하지 않은 스크랩")
        @Test
        void fail_scrap_not_found() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long contentId = 100L;

            // when
            ResultActions resultActions = mvc.perform(
                    post("/api/v0/scrap/delete/content/{content_id}", contentId)
                            .header(JwtProvider.HEADER, accessToken)
            );

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SCRAP_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SCRAP_NOT_FOUND.getStatus())
            );
        }
    }

    @DisplayName("스크랩 뷰 가져오기 컨트롤러 테스트")
    @Nested
    class GetScrapControllerTest{
        @DisplayName("성공 기본 페이지")
        @Test
        void success_default_page() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");

            // when
            ResultActions resultActions = mvc.perform(
                    get("/api/v0/scrap")
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.curSubscribeId").value(2L),
                    jsonPath("$.response.scraps[0].contentId").value(19L),
                    jsonPath("$.response.scraps[1].contentId").value(21L),
                    jsonPath("$.response.scraps[2].contentId").value(22L),
                    jsonPath("$.response.scraps[3].contentId").value(23L),
                    jsonPath("$.response.scraps[4].contentId").value(24L),
                    jsonPath("$.response.scraps[5].contentId").value(27L),
                    jsonPath("$.response.scraps[6].contentId").value(28L),
                    jsonPath("$.response.scraps[7].contentId").value(29L),
                    jsonPath("$.response.scraps[8].contentId").value(30L),
                    jsonPath("$.response.scraps[9].contentId").value(31L),
                    jsonPath("$.error").doesNotExist()
            );

            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(ResourceSnippetParameters.builder()
                            .description("스크랩 뷰 조회 API")
                            .requestHeaders(
                                    headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("access 토큰")
                            )
                            .pathParameters(
                                    parameterWithName("subscribe_id").type(SimpleType.INTEGER).optional().description("구독 ID")
                            )
                            .queryParameters(
                                    parameterWithName("page").type(SimpleType.INTEGER).optional().description("페이지")
                            )
                            .build())
            ));
        }

        @DisplayName("성공 기본 2페이지")
        @Test
        void success_default_2page() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            int page = 1;
            // when
            ResultActions resultActions = mvc.perform(
                    get("/api/v0/scrap?page={page}", page)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.curSubscribeId").value(2L),
                    jsonPath("$.response.scraps[0].contentId").value(32L),
                    jsonPath("$.response.scraps[1].contentId").value(33L),
                    jsonPath("$.error").doesNotExist()
            );
        }

        @DisplayName("성공 구독 선택 페이지")
        @Test
        void success_select_subscribe_page() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 2L;

            // when
            ResultActions resultActions = mvc.perform(
                    get("/api/v0/scrap/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.curSubscribeId").value(2L),
                    jsonPath("$.response.scraps[0].contentId").value(19L),
                    jsonPath("$.response.scraps[1].contentId").value(21L),
                    jsonPath("$.response.scraps[2].contentId").value(22L),
                    jsonPath("$.response.scraps[3].contentId").value(23L),
                    jsonPath("$.response.scraps[4].contentId").value(24L),
                    jsonPath("$.response.scraps[5].contentId").value(27L),
                    jsonPath("$.response.scraps[6].contentId").value(28L),
                    jsonPath("$.response.scraps[7].contentId").value(29L),
                    jsonPath("$.response.scraps[8].contentId").value(30L),
                    jsonPath("$.response.scraps[9].contentId").value(31L),
                    jsonPath("$.error").doesNotExist()
            );
        }

        @DisplayName("실패 빈 구독 목록")
        @Test
        void fail_empty_subscirbes() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh12@kakao.com");

            // when
            ResultActions resultActions = mvc.perform(
                    get("/api/v0/scrap" )
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_EMPTY.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_EMPTY.getStatus())
            );
        }

        @DisplayName("실패 사용자가 구독하지 않은 구독 ID")
        @Test
        void fail_user_not_subscribe() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");
            Long subscribeId = 100L;

            // when
            ResultActions resultActions = mvc.perform(
                    get("/api/v0/scrap/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("false"),
                    jsonPath("$.response").doesNotExist(),
                    jsonPath("$.error.message").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getMessage()),
                    jsonPath("$.error.status").value(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND.getStatus())
            );
        }
    }
}
