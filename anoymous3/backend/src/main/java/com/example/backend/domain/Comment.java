package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "comments")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private Post post;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private User author;

    @ManyToOne(fetch=FetchType.LAZY)
    private Comment parent;  // null이면 최상위

    @Column(nullable=false, columnDefinition = "LONGTEXT")
    private String content;  // 서버에서 sanitize한 HTML 저장 (또는 마크다운 HTML)

    @Column(nullable=false)
    private int depth; // 0=댓글, 1=대댓글, ... (원하면 최대 2로 제한)

    // ✅ 소프트 삭제 시각(null이면 살아있음)
    private Instant deletedAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
