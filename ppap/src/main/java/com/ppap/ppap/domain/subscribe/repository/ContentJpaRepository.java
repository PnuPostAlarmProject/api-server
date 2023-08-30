package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ContentJpaRepository extends JpaRepository<Content, Long>, CustomContentRepository{

    @Query(value = "select distinct(c.notice.id) from Content c")
    Set<Long> findAllDistinctNoticeId();

    @Query(value = "select c from Content c " +
            "where c.notice.id = :noticeId ",
    countQuery = "select count(c.id) from Content c " +
            "where c.notice.id = :noticeId")
    Page<Content> findByNoticeId(@Param("noticeId") Long noticeId, Pageable pageable);
}
