package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

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
}
