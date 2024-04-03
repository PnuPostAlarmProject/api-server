package com.ppap.ppap.domain.base.utils;

import java.time.LocalDateTime;

public record PageCursor<T>(
	T data,
	CursorRequest nextCursor,
	boolean hasNext
) {
	public PageCursor(T data, CursorRequest nextCursor) {
		this(data, nextCursor, !nextCursor.cursor().isEqual(LocalDateTime.MIN));
	}
}
