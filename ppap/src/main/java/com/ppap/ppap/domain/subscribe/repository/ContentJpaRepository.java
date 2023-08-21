package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ContentJpaRepository extends JpaRepository<Content, Long>, CustomContentRepository{

    @Query(value = "select distinct(c.notice.id) from Content c")
    Set<Long> findAllDistinctNoticeId();
}
