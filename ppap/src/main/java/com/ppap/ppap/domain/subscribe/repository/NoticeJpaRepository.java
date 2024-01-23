package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.constant.NoticeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository{

    Optional<Notice> findByLink(String link);

    @Query(value = "select n From Notice n "
        + "where n.noticeType = :noticeType")
    List<Notice> findByNoticeType(@Param("noticeType") NoticeType noticeType);
}
