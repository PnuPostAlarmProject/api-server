package com.ppap.ppap.domain.user;

import com.ppap.ppap.domain.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
