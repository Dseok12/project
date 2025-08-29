package com.example.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="users", indexes = {
        @Index(name="idx_users_email", columnList = "email", unique = true),
        @Index(name="idx_users_activityId", columnList = "activityId", unique = true)
})
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email @NotBlank
    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String passwordHash;

    @NotBlank
    @Column(nullable = false, unique = true, length = 32)
    private String activityId;

    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }
}
