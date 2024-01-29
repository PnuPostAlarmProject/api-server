package com.ppap.ppap.domain.subscribe.dto;

import java.time.LocalDateTime;

import com.ppap.ppap.domain.subscribe.entity.constant.NoticeType;

import lombok.Builder;

public record NoticeDto(
	String link,
	LocalDateTime lastNoticeTime,
	String title,
	NoticeType noticeType,
	String college,
	String department
) {
	@Builder
	public NoticeDto {
	}
}
