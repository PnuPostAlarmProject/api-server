package com.ppap.ppap.rss;

import lombok.Builder;

import java.time.LocalDateTime;

public record RssData(
        String title,
        String link,
        LocalDateTime pubDate,
        String author,
        String category
) {

    @Builder
    public RssData {
    }
}
