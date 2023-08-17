package com.ppap.ppap.domain.redis.entity;

import com.ppap.ppap.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60*60*24*365*10)
public class RefreshToken {

    @Id
    private String refreshToken;
    private Long userId;
    private String email;

    @Indexed
    private String accessToken;

    @Builder
    public RefreshToken(String refreshToken, Long userId, String email, String accessToken) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.accessToken = accessToken;
    }

    public static RefreshToken of(String refreshToken, String accessToken, User user) {
        return RefreshToken.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
