package com.ppap.ppap.domain.subscribe.entity;

import com.ppap.ppap._core.crawler.CrawlingData;
import com.ppap.ppap._core.crawler.RssData;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Column(nullable = false)
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

    public static Content of(CrawlingData crawlingData, Notice notice) {
        return Content.builder()
                .notice(notice)
                .title(crawlingData.title())
                .link(crawlingData.link())
                .pubDate(crawlingData.pubDate())
                .author(crawlingData.author())
                .category(crawlingData.category())
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
