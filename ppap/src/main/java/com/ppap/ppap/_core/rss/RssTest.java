package com.ppap.ppap._core.rss;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;

public class RssTest {

    private final static int feedCount = 20;

    // default 데이터 10개씩 가져옴
    private final static List<String> testRssLink = List.of(
            "https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/12549/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/12278/rssList.do",
            "https://chemeng.pusan.ac.kr/bbs/chemeng/2870/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2606/rssList.do",
            "https://french.pusan.ac.kr/bbs/french/4295/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2617/rssList.do",
            "https://ocean.pusan.ac.kr/bbs/ocean/2877/rssList.do",
            "https://fsn.pusan.ac.kr/bbs/fsn/2783/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2618/rssList.do",
            "https://molbiology.pusan.ac.kr/bbs/molbiology/3918/rssList.do",
            "https://phys.pusan.ac.kr/bbs/phys/2658/rssList.do",
            "https://sct.pusan.ac.kr/bbs/sct/17403/rssList.do",
            "https://nanomecha.pusan.ac.kr/bbs/nanomecha/3264/rssList.do",
            "https://biology.pusan.ac.kr/bbs/biology/3143/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2616/rssList.do",
            "https://pnuecon.pusan.ac.kr/bbs/pnuecon/3210/rssList.do",
            "https://biz.pusan.ac.kr/bbs/biz/2557/rssList.do",
            "https://eec.pusan.ac.kr/bbs/eehome/2650/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2605/rssList.do",
            "https://his.pusan.ac.kr/bbs/ee/2635/rssList.do",
            "https://ee.pusan.ac.kr/bbs/ee/2635/rssList.do"
    );

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SAXBuilder saxBuilder = new SAXBuilder();
        for(String rss : testRssLink) {
            try{
                Document document = saxBuilder.build(rss);
                Element root = document.getRootElement();
                Element channel = root.getChild("channel");
                List<Element> children = channel.getChildren("item");
                for(Element child: children) {
                    String title = child.getChildTextTrim("title");
                    String link = child.getChildTextTrim("link");
                    String pubDate = child.getChildTextTrim("pubDate");
                    String author = child.getChildTextTrim("author");
                    String category = child.getChildTextTrim("category");

                    RssData rssData = RssData.builder()
                            .title(title)
                            .link(link)
                            .pubDate(parseDateTime(pubDate))
                            .author(author)
                            .category(category)
                            .build();

//                        System.out.println(rssData);
                }
            // IOException안에 socketTimeout, socketConection 예외가 다 들어가 있다.
            } catch (JDOMException e) {
                e.printStackTrace();
                System.out.println("RSS(xml) 데이터를 읽을 수 없습니다.");
                break;
            } catch(IOException e) {
                e.printStackTrace();
                System.out.println("서버와의 연결이 비정상적으로 종료되었습니다.");
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println((double)(endTime - startTime)/1000 + "초");
    }

    public static LocalDateTime parseDateTime(String dateString) {
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
