package com.ppap.ppap.domain.notice.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "notice_tb")
public class Notice extends AuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(length = 255, nullable = false)
    String rssLink;

    // 선택사항
    @Column(length = 255)
    String noticeLink;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="last_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime lastNoticeTime;

    @Builder
    public Notice(Long id, String rssLink, String noticeLink, LocalDateTime lastNoticeTime) {
        this.id = id;
        this.rssLink = rssLink;
        this.noticeLink = noticeLink;
        this.lastNoticeTime = lastNoticeTime;
    }
}
