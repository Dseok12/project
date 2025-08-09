package com.example.backend.repository;

import com.example.backend.domain.Comment;
import com.example.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPost(Post post);
}
