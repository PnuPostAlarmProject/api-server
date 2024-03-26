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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ContentControllerTest extends RestDocs {

    @DisplayName("공지사항 내용 뷰 가져오기 통합 테스트")
    @Nested
    class GetContentControllerTest {
        @DisplayName("성공 기본 페이지")
        @Test
        void success_default_page() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh@naver.com");

            // when
            ResultActions resultActions = mvc.perform(
                    get("/api/v0/content")
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.curSubscribeId").value(2L),
                    jsonPath("$.response.contents[0].contentId").value(18L),
                    jsonPath("$.response.contents[0].isScraped").value("false"),
                    jsonPath("$.response.contents[1].contentId").value(19L),
                    jsonPath("$.response.contents[1].isScraped").value("true"),
                    jsonPath("$.response.contents[2].contentId").value(20L),
                    jsonPath("$.response.contents[2].isScraped").value("false"),
                    jsonPath("$.response.contents[3].contentId").value(21L),
                    jsonPath("$.response.contents[3].isScraped").value("true"),
                    jsonPath("$.response.contents[4].contentId").value(22L),
                    jsonPath("$.response.contents[4].isScraped").value("true"),
                    jsonPath("$.response.contents[5].contentId").value(23L),
                    jsonPath("$.response.contents[5].isScraped").value("true"),
                    jsonPath("$.response.contents[6].contentId").value(24L),
                    jsonPath("$.response.contents[6].isScraped").value("true"),
                    jsonPath("$.response.contents[7].contentId").value(25L),
                    jsonPath("$.response.contents[7].isScraped").value("false"),
                    jsonPath("$.response.contents[8].contentId").value(26L),
                    jsonPath("$.response.contents[8].isScraped").value("false"),
                    jsonPath("$.response.contents[9].contentId").value(27L),
                    jsonPath("$.response.contents[9].isScraped").value("true"),
                    jsonPath("$.error").doesNotExist()
            );

            resultActions.andDo(document(
                    snippet,
                    getDocumentRequest(),
                    getDocumentResponse(),
                    resource(
                            ResourceSnippetParameters.builder()
                                    .description("공지사항 뷰 API")
                                    .requestHeaders(
                                            headerWithName(JwtProvider.HEADER).type(SimpleType.STRING).description("accesss 토큰")
                                    )
                                    .pathParameters(
                                            parameterWithName("subscribe_id").type(SimpleType.INTEGER).optional().description("구독 ID")
                                    )
                                    .queryParameters(
                                            parameterWithName("page").type(SimpleType.INTEGER).optional().defaultValue(0).description("페이지")
                                    )
                                    .build()
                    )
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
                    get("/api/v0/content?page={page}", page)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.curSubscribeId").value(2L),
                    jsonPath("$.response.contents[0].contentId").value(28L),
                    jsonPath("$.response.contents[0].isScraped").value("true"),
                    jsonPath("$.response.contents[1].contentId").value(29L),
                    jsonPath("$.response.contents[1].isScraped").value("true"),
                    jsonPath("$.response.contents[2].contentId").value(30L),
                    jsonPath("$.response.contents[2].isScraped").value("true"),
                    jsonPath("$.response.contents[3].contentId").value(31L),
                    jsonPath("$.response.contents[3].isScraped").value("true"),
                    jsonPath("$.response.contents[4].contentId").value(32L),
                    jsonPath("$.response.contents[4].isScraped").value("true"),
                    jsonPath("$.response.contents[5].contentId").value(33L),
                    jsonPath("$.response.contents[5].isScraped").value("true"),
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
                    get("/api/v0/content/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//            System.out.println(responseBody);

            // then
            resultActions.andExpectAll(
                    jsonPath("$.success").value("true"),
                    jsonPath("$.response.curSubscribeId").value(2L),
                    jsonPath("$.response.contents[0].contentId").value(18L),
                    jsonPath("$.response.contents[0].isScraped").value("false"),
                    jsonPath("$.response.contents[1].contentId").value(19L),
                    jsonPath("$.response.contents[1].isScraped").value("true"),
                    jsonPath("$.response.contents[2].contentId").value(20L),
                    jsonPath("$.response.contents[2].isScraped").value("false"),
                    jsonPath("$.response.contents[3].contentId").value(21L),
                    jsonPath("$.response.contents[3].isScraped").value("true"),
                    jsonPath("$.response.contents[4].contentId").value(22L),
                    jsonPath("$.response.contents[4].isScraped").value("true"),
                    jsonPath("$.response.contents[5].contentId").value(23L),
                    jsonPath("$.response.contents[5].isScraped").value("true"),
                    jsonPath("$.response.contents[6].contentId").value(24L),
                    jsonPath("$.response.contents[6].isScraped").value("true"),
                    jsonPath("$.response.contents[7].contentId").value(25L),
                    jsonPath("$.response.contents[7].isScraped").value("false"),
                    jsonPath("$.response.contents[8].contentId").value(26L),
                    jsonPath("$.response.contents[8].isScraped").value("false"),
                    jsonPath("$.response.contents[9].contentId").value(27L),
                    jsonPath("$.response.contents[9].isScraped").value("true"),
                    jsonPath("$.error").doesNotExist()
            );
        }

        @DisplayName("실패 빈 구독 목록")
        @Test
        void fail_empty_subscribe_list() throws Exception {
            // given
            String accessToken = getAccessToken("rjsdnxogh12@kakao.com");

            // when
            ResultActions resultActions = mvc.perform(
                    get("/api/v0/content")
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//            System.out.println(responseBody);

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
                    get("/api/v0/content/{subscribe_id}", subscribeId)
                            .header(JwtProvider.HEADER, accessToken)
            );
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//            System.out.println(responseBody);

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
