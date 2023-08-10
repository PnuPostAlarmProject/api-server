package com.ppap.ppap.domain.notice.repository;

import com.ppap.ppap.domain.notice.entity.Notice;
import com.ppap.ppap.domain.notice.entity.Subscribe;
import com.ppap.ppap.domain.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    boolean existsByUserAndNotice(User user, Notice notice);
}
