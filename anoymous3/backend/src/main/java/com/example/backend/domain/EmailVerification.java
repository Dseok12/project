package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="email_verifications", indexes = {
        @Index(name="idx_ev_email", columnList = "email", unique = true)
})
public class EmailVerification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=180)
    private String email;

    @Column(nullable=false, length=6)
    private String code;

    @Column(nullable=false)
    private Instant expiresAt;   // 코드 만료(발송 +1h)

    private Instant verifiedAt;  // 검증 성공 시각

    private Instant updatedAt;

    @PrePersist @PreUpdate
    void touch() { updatedAt = Instant.now(); }
}
