// backend/src/main/java/com/example/backend/domain/Post.java
package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;

@Entity
@Table(
        name = "posts",
        indexes = {
                @Index(name = "idx_posts_notice_created_at", columnList = "notice,createdAt"),
                @Index(name = "idx_posts_created_at", columnList = "createdAt")
        }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@SQLDelete(sql = "UPDATE posts SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL") // 기본 조회에서 소프트 삭제 제외
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    // 아주 긴 본문 저장
    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    /** 공지 여부 (ADMIN 전용) */
    @Column(nullable = false)
    private boolean notice = false;

    /** 소프트 삭제 시각(null이면 살아있음) */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

    /* ===== 편의 메소드 ===== */

    /** 소프트 삭제 표시 (서비스/관리자 로직에서 필요 시 호출) */
    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    /** 삭제 여부 */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
