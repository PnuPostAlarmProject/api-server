package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ContentJpaRepository extends JpaRepository<Content, Long>, CustomContentRepository{

    @Query(value = "select distinct(c.notice.id) from Content c "
        + "where c.notice in :noticeList")
    Set<Long> findDistinctNoticeIdIn(@Param("noticeList") List<Notice> noticeList);

    @Query(value = "select c from Content c " +
            "where c.notice.id = :noticeId ",
    countQuery = "select count(c.id) from Content c " +
            "where c.notice.id = :noticeId")
    Page<Content> findByNoticeId(@Param("noticeId") Long noticeId, Pageable pageable);

    @Query(value = "select c from Content c " +
        "where c.notice.id = :noticeId ")
    Slice<Content> findByNoticeIdUsingCursorFirstPage(@Param("noticeId") Long noticeId, Pageable pageable);

    @Query(value = "select c from Content c "
        + "where c.pubDate <:cursor and c.notice.id = :noticeId ")
    Slice<Content> findByNoticeIdUsingCursor(@Param("noticeId") Long noticeId,
                                            @Param("cursor") LocalDateTime cursor,
                                            Pageable pageable);
}
