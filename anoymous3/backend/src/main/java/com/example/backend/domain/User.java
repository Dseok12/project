// backend/src/main/java/com/example/backend/domain/User.java
package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name="uk_users_email", columnNames = "email"),
        @UniqueConstraint(name="uk_users_activity_id", columnNames = "activity_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=190)
    private String email;

    @Column(name="activity_id", nullable=false, length=64)
    private String activityId;

    @Column(nullable=false)
    private String passwordHash;

    // --- 권한/상태 필드 추가 ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;                 // USER / ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE; // ACTIVE / SUSPENDED / DELETED

    @Column(name = "suspended_until")
    private Instant suspendedUntil;                // 정지 해제 시각(무기한 정지면 null)

    @Column(name = "deleted_at")
    private Instant deletedAt;                     // 탈퇴(삭제) 시각

    @PrePersist
    void prePersist() {
        if (role == null) role = Role.USER;
        if (status == null) status = UserStatus.ACTIVE;
    }
}
