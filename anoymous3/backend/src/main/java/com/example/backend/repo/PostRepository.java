package com.example.backend.repo;

import com.example.backend.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = "author")
    Optional<Post> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "author")
    Page<Post> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Post> findByAuthor_EmailIgnoreCase(String email, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Post> findByAuthor_ActivityIdIgnoreCase(String activityId, Pageable pageable);
}
