package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "post_reactions",
        uniqueConstraints = @UniqueConstraint(name="uk_reaction_post_user", columnNames = {"post_id","user_id"}))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PostReaction {
    public enum Type { LIKE, DISLIKE }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private Post post;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private Type type;

    @CreationTimestamp
    private Instant createdAt;
}
