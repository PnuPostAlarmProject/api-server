package com.ppap.ppap.domain.subscribe.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "content_tb", indexes = {
        @Index(name="idx_content_notice", columnList = "notice_id")
})
public class Content extends AuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false, unique = true)
    private Notice notice;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private LocalDateTime pubDate;

    @Column(length = 50)
    private String author;

    @Column(length = 50)
    private String category;

    @Builder
    public Content(Long id, Notice notice, String title, String link, LocalDateTime pubDate, String author, String category) {
        this.id = id;
        this.notice = notice;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.author = author;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(getId(), content.getId()) && Objects.equals(getNotice(), content.getNotice()) && Objects.equals(getTitle(), content.getTitle()) && Objects.equals(getLink(), content.getLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNotice(), getTitle(), getLink());
    }
}
