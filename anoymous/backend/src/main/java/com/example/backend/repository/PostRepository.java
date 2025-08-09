package com.example.backend.repository;

import com.example.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PostRepository extends JpaRepository<Post,Long> {
}
