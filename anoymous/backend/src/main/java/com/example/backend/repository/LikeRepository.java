package com.example.backend.repository;

import com.example.backend.domain.Post;
import com.example.backend.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<PostLike,Long> {
    Optional<PostLike> findByPostAndUsername(Post post, String username);
}
