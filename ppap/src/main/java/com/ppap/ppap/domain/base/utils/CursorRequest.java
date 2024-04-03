package com.ppap.ppap.domain.base.utils;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CursorRequest(
	LocalDateTime cursor,
	@Positive @NotNull(message = "page 크기는 반드시 입력하셔야 합니다.") Integer pageSize
) {
	public static final LocalDateTime NONE_CURSOR = LocalDateTime.MIN;
	public static final int DEFAULT_PAGE_SIZE = 10;

	public boolean hasCursor(){
		return cursor != null;
	}
	public CursorRequest next(LocalDateTime nextCursor) {
		return new CursorRequest(nextCursor, pageSize);
	}
}
