// com.example.backend.domain.User
package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name="uk_users_email", columnNames = "email"),
        @UniqueConstraint(name="uk_users_activity_id", columnNames = "activity_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=190)
    private String email;

    @Column(name="activity_id", nullable=false, length=64)
    private String activityId;

    @Column(nullable=false)
    private String passwordHash;

    // ====== 권한/상태 ======
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role = Role.USER;            // USER / ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private UserStatus status = UserStatus.ACTIVE; // ACTIVE / SUSPENDED / DELETED

    private Instant suspendedUntil;           // 정지 해제 시점(없으면 null)
    private Instant deletedAt;                // 소프트 삭제(없으면 null)

    // ====== 생성 시각 자동 기록 ======
    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (role == null) role = Role.USER;
        if (status == null) status = UserStatus.ACTIVE;
    }
}
