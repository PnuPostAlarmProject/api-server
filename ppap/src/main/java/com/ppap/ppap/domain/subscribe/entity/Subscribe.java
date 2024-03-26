package com.ppap.ppap.domain.subscribe.entity;

import com.ppap.ppap._core.utils.converter.NullToMaxIntegerConverter;
import com.ppap.ppap.domain.base.entity.AuditingEntity;
import com.ppap.ppap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user", "notice"}, callSuper = true)
@Entity
@Getter
@Table(name = "subscribe_tb", uniqueConstraints = {
        @UniqueConstraint(name = "uk_subscribe_user_notice",columnNames = {"user_id", "notice_id"})
})
@DynamicUpdate
public class Subscribe extends AuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(length = 255)
    private String noticeLink;

    @Column(nullable = false)
    private Boolean isActive;

    // default값은 정수 최대값 -> 우선순위 제일 낮음
    @Convert(converter = NullToMaxIntegerConverter.class)
    private Integer priority = Integer.MAX_VALUE;

    @Builder
    public Subscribe(Long id, String title, User user, Notice notice, String noticeLink, Boolean isActive, Integer priority) {
        this.id = id;
        this.title = title;
        this.user = user;
        this.notice = notice;
        this.noticeLink = noticeLink;
        this.isActive = isActive;
        this.priority = Objects.isNull(priority) ? Integer.MAX_VALUE : priority;
    }

    public static Subscribe of(User user, Notice notice, String title, String noticeLink, Boolean isActive) {
        return Subscribe.builder()
                .user(user)
                .notice(notice)
                .title(title)
                .noticeLink(noticeLink)
                .isActive(isActive)
                .build();
    }

    public void changeNoticeLinkAndTitle(String noticeLink, String title) {
        this.noticeLink = noticeLink;
        this.title = title;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeActive(){
        this.isActive = !this.isActive;
    }

    public void changePriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscribe subscribe = (Subscribe) o;
        return Objects.equals(getId(), subscribe.getId()) && Objects.equals(getTitle(), subscribe.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle());
    }
}
