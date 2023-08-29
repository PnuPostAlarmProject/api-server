package com.ppap.ppap.domain.subscribe.entity;

import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap.domain.base.entity.AuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@ToString(exclude = {"notice"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "content_tb", indexes = {
        @Index(name="idx_content_notice", columnList = "notice_id"),
        @Index(name="idx_content_pubdate", columnList = "pub_date")
}, uniqueConstraints = {
        @UniqueConstraint(name="uk_content_notice_link", columnNames = {"notice_id", "link"})
})
public class Content {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private String link;

    @Column(name = "pub_date", nullable = false)
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

    public static Content from(RssData rssData, Notice notice) {
        return Content.builder()
                .notice(notice)
                .title(rssData.title())
                .link(rssData.link())
                .pubDate(rssData.pubDate())
                .author(rssData.author())
                .category(rssData.category())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(getId(), content.getId()) && Objects.equals(getNotice(), content.getNotice()) && Objects.equals(getTitle(), content.getTitle()) && Objects.equals(getLink(), content.getLink()) && Objects.equals(getPubDate(), content.getPubDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNotice(), getTitle(), getLink(), getPubDate());
    }
}
