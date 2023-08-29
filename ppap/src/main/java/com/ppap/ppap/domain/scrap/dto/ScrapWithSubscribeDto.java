package com.ppap.ppap.domain.scrap.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record ScrapWithSubscribeDto(
    String title,
    String noticeTitle,
    String link,
    LocalDateTime pubDate
) {
    @Builder
    public ScrapWithSubscribeDto {
    }
}
