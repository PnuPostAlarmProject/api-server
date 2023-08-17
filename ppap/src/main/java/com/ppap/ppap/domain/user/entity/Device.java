package com.ppap.ppap.domain.user.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "device_tb")
public class Device extends AuditingEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false)
    private String fcmToken;

    @Builder
    public Device(Long id, User user, String fcmToken) {
        this.id = id;
        this.user = user;
        this.fcmToken = fcmToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(getId(), device.getId()) && Objects.equals(getUser(), device.getUser()) && Objects.equals(getFcmToken(), device.getFcmToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getFcmToken());
    }
}
