package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.dto.query.FindByUnivAndRoleQueryDto;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.entity.constant.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubscribeJpaRepository extends JpaRepository<Subscribe, Long> {

    boolean existsByUserAndNotice(User user, Notice notice);

    List<Subscribe> findByUserId(Long userId);

    @Query(value = "select s from Subscribe s " +
            "join fetch s.notice n " +
            "where s.user.id = :userId")
    List<Subscribe> findByUserIdFetchJoinNotice(@Param("userId") Long userId);

    @Query(value = "select s from Subscribe s " +
            "where s.notice.id = :noticeId and s.isActive = true")
    List<Subscribe> findByNoticeId(@Param("noticeId") Long noticeId);

    @Query(value = "select s from Subscribe s " +
            "join fetch s.notice n " +
            "where s.id = :subscribeId")
    Optional<Subscribe> findByIdFetchJoinNotice(@Param("subscribeId") Long subscribeId);

    @Query(value = "select s from Subscribe s "
        + "where s.notice.id in (:noticeIds) and s.isActive=true")
    Set<Subscribe> findByNoticeIdInAndIsActive(@Param("noticeIds")Collection<Long> noticeIds);

    @Query(value = "select new com.ppap.ppap.domain.subscribe.dto.query.FindByUnivAndRoleQueryDto"
        + "(s.id, s.title, s.notice.link) "
        + "from Subscribe s "
        + "join s.notice n "
        + "join s.user u "
        + "where n.univ.id = :univId and u.role = :role "
        + "order by s.id")
    List<FindByUnivAndRoleQueryDto> findByUnivIdAndRoleFetchJoin(@Param("univId") Long univId, @Param("role") Role role);
}
