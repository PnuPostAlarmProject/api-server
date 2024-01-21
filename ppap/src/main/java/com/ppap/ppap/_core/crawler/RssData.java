package com.ppap.ppap._core.crawler;

import lombok.Builder;

import java.time.LocalDateTime;

public record RssData(
        String title,
        String link,
        LocalDateTime pubDate,
        String author,
        String category
)implements CrawlingData {

    @Builder
    public RssData {
    }
}
