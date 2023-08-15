package com.ppap.ppap.service.rss;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap._core.rss.RssReader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@DisplayName("RssReader 테스트")
@ExtendWith(MockitoExtension.class)
public class RssReaderServiceTest {
    @InjectMocks
    private RssReader rssReader;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private SAXBuilder saxBuilder;

    @DisplayName("https 링크만들기 & 쿼리스트링 지우기 테스트")
    @Nested
    class MakeHttpsAndRemoveQueryStringTest {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            String httpString = "http://cse.pusan.ac.kr/bbs/cse/2615/rssList.do?row=50";

            // when
            String result = rssReader.makeHttpsAndRemoveQueryString(httpString);

            // then
            assertEquals("https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do", result);
        }
    }

    @DisplayName("Rss 링크 검증 테스트")
    @Nested
    class ValidRssLinkTest{
        @DisplayName("성공")
        @Test
        void success() throws IOException, JDOMException {
            // given
            String httpString = "http://cse.pusan.ac.kr/bbs/cse/2615/rssList.do?row=50";

            // mock

            // when
            rssReader.validRssLink(httpString);

            // then
            verify(saxBuilder).build(anyString());
        }

        @DisplayName("실패 부산대학교가 아닌 링크")
        @Test
        void fail_not_pnu_link() throws IOException, JDOMException {
            // given
            String httpString = "https://www.mois.go.kr/gpms/view/jsp/rss/rss.jsp?ctxCd=1012";

            // mock

            // when
            Throwable exception = assertThrows(Exception400.class, () -> rssReader.validRssLink(httpString));

            // then
            assertEquals(BaseExceptionStatus.RSS_LINK_NOT_PNU.getMessage(), exception.getMessage());


        }

        @DisplayName("실패 rssList.do가 아닌 링크")
        @Test
        void fail_not_rssList_do() throws IOException, JDOMException {
            // given
            String httpString = "https://cse.pusan.ac.kr/cse/14651/subview.do";

            // mock

            // when
            Throwable exception = assertThrows(Exception400.class, () -> rssReader.validRssLink(httpString));

            // then
            assertEquals(BaseExceptionStatus.RSS_LINK_INVALID.getMessage(), exception.getMessage());
        }
    }


    // getRSSData 서비스 코드 작성해야함
    @DisplayName("getRssData 테스트")
    @Nested
    class GetRssDataTest {
        @DisplayName("성공")
        @Test
        void success() throws IOException, JDOMException {
            // given
            String httpLink = "http://cse.pusan.ac.kr/bbs/cse/2615/rssList.do?row=50";
            File file = new File("src/test/java/com/ppap/ppap/_core/resources/testRss.txt");
            SAXBuilder saxTestBuilder = new SAXBuilder();
            Document document = saxTestBuilder.build(file);

            // mock
            given(saxBuilder.build(httpLink)).willReturn(document);

            // when
            List<RssData> resultDatas = rssReader.getRssData(httpLink);

            // then
            assertEquals(10, resultDatas.size());
        }

        @DisplayName("실패 부산대학교가 아닌 링크")
        @Test
        void fail_not_pnu_link() throws IOException, JDOMException {
            // given
            String httpString = "https://www.mois.go.kr/gpms/view/jsp/rss/rss.jsp?ctxCd=1012";

            // mock

            // when
            Throwable exception = assertThrows(Exception400.class, () -> rssReader.getRssData(httpString));

            // then
            assertEquals(BaseExceptionStatus.RSS_LINK_NOT_PNU.getMessage(), exception.getMessage());


        }

        @DisplayName("실패 rssList.do가 아닌 링크")
        @Test
        void fail_not_rssList_do() throws IOException, JDOMException {
            // given
            String httpString = "https://cse.pusan.ac.kr/cse/14651/subview.do";

            // mock

            // when
            Throwable exception = assertThrows(Exception400.class, () -> rssReader.getRssData(httpString));

            // then
            assertEquals(BaseExceptionStatus.RSS_LINK_INVALID.getMessage(), exception.getMessage());
        }
    }
}
