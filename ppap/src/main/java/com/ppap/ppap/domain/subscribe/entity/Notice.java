package com.ppap.ppap.domain.subscribe.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import com.ppap.ppap.domain.subscribe.entity.constant.NoticeType;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "notice_tb")
@DynamicUpdate
public class Notice extends AuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(length = 255, nullable = false)
    private String link;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="last_time", nullable = false)
    private LocalDateTime lastNoticeTime;

    @Enumerated(value = EnumType.STRING)
    @Column(length=20, nullable = false)
    private NoticeType noticeType;

    @Builder
    public Notice(Long id, String link, LocalDateTime lastNoticeTime, NoticeType noticeType) {
        this.id = id;
        this.link = link;
        this.lastNoticeTime = lastNoticeTime;
        this.noticeType = Objects.nonNull(noticeType) ? noticeType : NoticeType.RSS;
    }

    public static Notice of(String rssLink) {
        return Notice.builder()
                .link(rssLink)
                .lastNoticeTime(LocalDateTime.now().minusDays(5))
                .build();
    }

    public void changeLastNoticeTime(LocalDateTime lastNoticeTime) {
        this.lastNoticeTime = lastNoticeTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return Objects.equals(getId(), notice.getId()) && Objects.equals(getLink(), notice.getLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLink());
    }
}
