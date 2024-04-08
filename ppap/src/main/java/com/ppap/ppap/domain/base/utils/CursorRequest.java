package com.ppap.ppap.domain.base.utils;

import java.time.LocalDateTime;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;

public record CursorRequest(
	LocalDateTime cursor,
	int pageSize
) {
	public static final LocalDateTime NONE_CURSOR = LocalDateTime.MIN;

	public CursorRequest(LocalDateTime cursor, int pageSize) {
		this.cursor=cursor;
		this.pageSize=validPageSize(pageSize);
	}

	public boolean hasCursor(){
		return cursor != null;
	}

	public CursorRequest next(LocalDateTime nextCursor) {
		return new CursorRequest(nextCursor, pageSize);
	}

	private int validPageSize(int pageSize) {
		if (pageSize <= 0) {
			throw new Exception400(BaseExceptionStatus.PAGE_SIZE_NOT_POSITIVE);
		} else if(pageSize > 50) {
			throw new Exception400(BaseExceptionStatus.PAGE_SIZE_IS_UNDER_50);
		}
		return pageSize;
	}
}
