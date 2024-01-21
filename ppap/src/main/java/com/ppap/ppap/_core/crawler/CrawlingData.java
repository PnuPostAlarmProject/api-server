package com.ppap.ppap._core.crawler;

import java.time.LocalDateTime;

public interface CrawlingData {

	String title();
	String link();
	LocalDateTime pubDate();
	String author();
	String category();
}
