package com.ppap.ppap.domain.base.utils;


public record PageCursor<T>(
	T data,
	CursorRequest nextCursor,
	boolean hasNext
) {
	public PageCursor(T data, CursorRequest nextCursor, int dataSize) {
		this(data, nextCursor, findHasNext(dataSize, nextCursor));
	}

	public static boolean findHasNext(int dataSize, CursorRequest nextCursor) {
		if (dataSize < nextCursor.pageSize()) {
			return false;
		}
		return !nextCursor.cursor().isEqual(CursorRequest.NONE_CURSOR);
	}
}
