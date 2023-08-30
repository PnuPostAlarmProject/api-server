package com.ppap.ppap._core.rss;

import com.ppap.ppap.domain.subscribe.entity.Notice;
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
