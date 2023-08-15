package com.ppap.ppap.domain.subscribe.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;


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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="last_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime lastNoticeTime;

    @Builder
    public Notice(Long id, String rssLink, LocalDateTime lastNoticeTime) {
        this.id = id;
        this.rssLink = rssLink;
        this.lastNoticeTime = lastNoticeTime;
    }

    public static Notice of(String rssLink) {
        return Notice.builder()
                .rssLink(rssLink)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return Objects.equals(getId(), notice.getId()) && Objects.equals(getRssLink(), notice.getRssLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRssLink());
    }
}
