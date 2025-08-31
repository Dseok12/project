package com.example.backend.repo;

import com.example.backend.domain.PostReaction;
import com.example.backend.domain.PostReaction.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    long countByPost_IdAndType(Long postId, Type type);
    Optional<PostReaction> findByPost_IdAndUser_Id(Long postId, Long userId);
}
