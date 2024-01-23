package com.ppap.ppap.domain.subscribe.repository;

import java.util.Collection;
import java.util.List;

import com.ppap.ppap.domain.subscribe.entity.Notice;

public interface CustomNoticeRepository {
	void updateAll(Collection<Notice> noticeList);
}
