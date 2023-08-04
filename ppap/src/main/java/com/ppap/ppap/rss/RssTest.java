package com.ppap.ppap.rss;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RssTest {

    private final static int feedCount = 20;

    public static void main(String[] args) {
        String url = "https://cse.pusan.ac.kr/bbs/cse/2605/rssList.do?row="+ feedCount ;


        SAXBuilder saxBuilder = new SAXBuilder();
        try{
            Document document = saxBuilder.build(url);
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

                System.out.println(rssData);
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public static LocalDateTime parseDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        int dotIndex = dateString.lastIndexOf(".");
        int nanoSeconds = 0;

        if (dotIndex != -1 && dotIndex + 1 < dateString.length()) {
            try {
                nanoSeconds = Integer.parseInt(dateString.substring(dotIndex + 1));
            } catch (NumberFormatException e) {
                // Ignore and keep nanoSeconds as 0.
            }
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateString.substring(0, dotIndex + 1) + String.format("%03d", nanoSeconds), formatter);
            return dateTime;
        } catch (Exception e) {
            return null;
        }
    }
}
