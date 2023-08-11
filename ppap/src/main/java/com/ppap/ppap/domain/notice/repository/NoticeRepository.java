package com.ppap.ppap.domain.notice.repository;

import com.ppap.ppap.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findByRssLink(String rssLink);
}
