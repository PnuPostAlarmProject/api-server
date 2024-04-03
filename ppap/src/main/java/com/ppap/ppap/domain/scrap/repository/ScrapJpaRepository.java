package com.ppap.ppap.domain.scrap.repository;

import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    // 해당 경우와 같이 Scrap이 아닌 Content의 데이터에 정렬기준이 있으면 pageable이 아닌 직접 order by를 사용해야한다.
    @Query(value = "select s from Scrap s " +
            "join fetch s.content c " +
            "where s.user.id = :userId and c.notice.id = :noticeId " +
            "order by c.pubDate desc",
            countQuery = "select count(s) from Scrap s " +
                    "join s.content c " +
                    "where s.user.id = :userId and c.notice.id = :noticeId")
    Page<Scrap> findByUserIdAndNoticeIdFetchJoinContent(@Param("userId") Long userId,
                                                        @Param("noticeId") Long noticeId,
                                                        Pageable pageable);

    @Query(value = "select s from Scrap s " +
        "join fetch s.content c " +
        "where s.user.id = :userId and c.notice.id = :noticeId " +
        "order by c.pubDate desc")
    Slice<Scrap> findByUserIdAndNoticeIdFetchJoinContentCursorFirstPage(@Param("userId") Long userId,
        @Param("noticeId") Long noticeId,
        Pageable pageable);

    @Query(value = "select s from Scrap s " +
        "join fetch s.content c " +
        "where s.user.id = :userId and c.notice.id = :noticeId " +
        "and c.pubDate < :cursor " +
        "order by c.pubDate desc")
    Slice<Scrap> findByUserIdAndNoticeIdFetchJoinContentCursor(@Param("userId") Long userId,
        @Param("noticeId") Long noticeId,
        @Param("cursor") LocalDateTime cursor,
        Pageable pageable);

    Boolean existsByUserIdAndContentId(@Param("userId") Long userId,
                                       @Param("noticeId") Long contentId);
}
