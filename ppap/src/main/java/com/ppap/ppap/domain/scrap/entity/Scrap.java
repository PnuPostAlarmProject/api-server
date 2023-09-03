package com.ppap.ppap.domain.scrap.entity;

import com.ppap.ppap.domain.base.entity.AuditingEntity;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user", "content"})
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Builder
    public Scrap(Long id, User user, Content content) {
        this.id = id;
        this.user = user;
        this.content = content;
    }

    public static Scrap of(User user, Content content) {
        return Scrap.builder()
                .user(user)
                .content(content)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scrap scrap = (Scrap) o;
        return Objects.equals(getId(), scrap.getId()) && Objects.equals(getUser().getId(), scrap.getUser().getId()) && Objects.equals(getContent().getId(), scrap.getContent().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser().getId(), getContent().getId());
    }
}
