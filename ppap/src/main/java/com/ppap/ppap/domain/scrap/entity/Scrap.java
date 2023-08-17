package com.ppap.ppap.domain.scrap.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "scrap_tb", uniqueConstraints = {
        @UniqueConstraint(name = "uk_scrap_user_content", columnNames = {"user_id", "content_id"})
})
public class Scrap extends AuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;


}
