package com.ppap.ppap.domain.notice.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import com.ppap.ppap.domain.user.Entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "subscribe_tb", uniqueConstraints = {
        @UniqueConstraint(name = "uk_subscribe_user_notice",columnNames = {"user_id", "notice_id"})
})
public class Subscribe extends AuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Builder
    public Subscribe(Long id, User user, Notice notice) {
        this.id = id;
        this.user = user;
        this.notice = notice;
    }
}
