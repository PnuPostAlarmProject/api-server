package com.ppap.ppap._core.crawler;

import java.time.LocalDateTime;

import lombok.Builder;

public record JsoupData(
	String title,
	String link,
	LocalDateTime pubDate,
	String author,
	String category
)implements CrawlingData {
	@Builder
	public JsoupData {
	}
}
