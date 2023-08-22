package com.ppap.ppap._core.rss;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap._core.exception.Exception502;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class RssReader {
    private final int CONNECTION_TIMEOUT = 5000;
    private final int READ_TIMEOUT = 10000;
    private final ObjectProvider<SAXBuilder> saxBuilderProvider;
    private final UrlFactory urlFactory;

    public String makeHttpsAndRemoveQueryString(String url) {
        return url.replace("http://","https://")
                .split("\\?")[0];
    }

    public void validRssLink(String makeHttpsAndRemoveQueryString) {
        validPnuAndRssLink(makeHttpsAndRemoveQueryString);
        try {
            InputStream inputStream = urlFactory.getInputStream(makeHttpsAndRemoveQueryString +"?row=1", CONNECTION_TIMEOUT, READ_TIMEOUT);
            SAXBuilder saxBuilder = saxBuilderProvider.getObject();
            Document document = saxBuilder.build(inputStream);
        } catch (JDOMException | MalformedURLException e) {
            throw new Exception400(BaseExceptionStatus.RSS_LINK_INVALID);
        } catch(SocketException | SocketTimeoutException e){
            throw new Exception502(BaseExceptionStatus.RSS_LINK_NETWORK_ERROR);
        } catch (IOException e) {
            log.error("error :", e);
            throw new Exception500(BaseExceptionStatus.RSS_LINK_UNKNOWN_ERROR);
        }
    }

    // sax를 싱글톤으로 바꾸면 경합조건이 발생해서 threadSafe하지 못하게 변한다.
    public List<RssData> getRssData(String rssLink, boolean isInit){
        List<RssData> rssDataList = new ArrayList<>();
        try {
            InputStream inputStream = urlFactory.getInputStream(isInit ? rssLink +"?row=30" : rssLink, CONNECTION_TIMEOUT, READ_TIMEOUT);
            SAXBuilder saxBuilder = saxBuilderProvider.getObject();
            Document document = saxBuilder.build(inputStream);
            Element root = document.getRootElement();
            Element channel = root.getChild("channel");
            List<Element> children = channel.getChildren("item");
            for(Element child: children) {
                String title = child.getChildTextTrim("title");
                String link = child.getChildTextTrim("link");
                String pubDate = child.getChildTextTrim("pubDate");
                String author = child.getChildTextTrim("author");
                String category = child.getChildTextTrim("category");

                rssDataList.add(RssData.builder()
                        .title(title)
                        .link(link)
                        .pubDate(parseDateTime(pubDate))
                        .author(author)
                        .category(category)
                        .build());
            }
        } catch (JDOMException | MalformedURLException e) {
            log.error("error :", e);
            throw new Exception400(BaseExceptionStatus.RSS_LINK_INVALID);
        } catch(SocketException | SocketTimeoutException e){
            throw new Exception502(BaseExceptionStatus.RSS_LINK_NETWORK_ERROR);
        } catch (IOException e) {
            log.error("error :", e);
            throw new Exception500(BaseExceptionStatus.RSS_LINK_UNKNOWN_ERROR);
        }

        return rssDataList;
    }


    /*
     * 부산대학교 RSS 링크인지 확인하는 메소드 조건에 맞지 않으면 예외를 발생시킨다.
     */
    private void validPnuAndRssLink(String url){
        if(!url.contains("pusan.ac.kr")) {
            throw new Exception400(BaseExceptionStatus.RSS_LINK_NOT_PNU);
        }

        if(!url.contains("rssList.do")) {
            throw new Exception400(BaseExceptionStatus.RSS_LINK_INVALID);
        }
    }

    /**
     * "yyyy-MM-dd HH:mm:ss.SSS"형식의 날짜를 LocalDateTime으로 변환시켜준다.
     * 이때 Rss 데이터로 가져온 값이 다음과 같기에 해당 코드가 있다.
     * [yyyy-MM-dd HH:mm:ss.SSS, yyyy-MM-dd HH:mm:ss.SS, yyyy-MM-dd HH:mm:ss.S]
     */
    private LocalDateTime parseDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        int dotIndex = dateString.lastIndexOf(".");
        int milliSeconds = 0;

        if (dotIndex != -1 && dotIndex + 1 < dateString.length()) {
            try {
                milliSeconds = Integer.parseInt(dateString.substring(dotIndex + 1));
            } catch (NumberFormatException e) {
                // Ignore and keep milliSeconds as 0.
            }
        }

        try {
            return LocalDateTime.parse(dateString.substring(0, dotIndex + 1) + String.format("%03d", milliSeconds), formatter);
        } catch (Exception e) {
            return null;
        }
    }
}
