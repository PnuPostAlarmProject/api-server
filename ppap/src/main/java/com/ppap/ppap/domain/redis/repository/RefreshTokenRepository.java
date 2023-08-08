package com.ppap.ppap.domain.redis.repository;

import com.ppap.ppap.domain.redis.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findById(String refreshToken);
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
