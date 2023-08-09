package com.ppap.ppap.domain.user.Entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import com.ppap.ppap.domain.user.Entity.constant.Provider;
import com.ppap.ppap.domain.user.Entity.constant.Role;
import com.ppap.ppap.domain.user.dto.oauth.OauthUserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_tb")
public class User extends AuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Builder
    public User(Long id, String email, String password, Role role, Provider provider) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider==null ? Provider.PROVIDER_NORMAL : provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail());
    }
}
