package com.ppap.ppap.domain.scrap.repository;

import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ScrapJpaRepository extends JpaRepository<Scrap, Long> {

    @Query(value = "select s from Scrap s " +
            "where s.user.id = :userId and s.content.id in (:contentIds)")
    List<Scrap> findByContentIds(@Param("userId") Long userId, @Param("contentIds") List<Long> contentIds);

    @Query(value = "select s from Scrap s " +
            "where s.user.id = :userId and s.content.id = :contentId")
    Optional<Scrap> findByContentIdAndUserId(@Param("userId") Long userId, @Param("contentId") Long contentId);
}
