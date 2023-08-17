package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    boolean existsByUserAndNotice(User user, Notice notice);

    List<Subscribe> findByUserId(Long userId);

    @Query(value = "select s from Subscribe s " +
            "join fetch s.notice n " +
            "where s.id = :subscribeId")
    Optional<Subscribe> findByIdFetchJoinNotice(Long subscribeId);
}
