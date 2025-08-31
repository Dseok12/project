// backend/src/main/java/com/example/backend/repo/CommentRepository.java
package com.example.backend.repo;

import com.example.backend.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author", "parent", "post"})
    List<Comment> findByPost_IdOrderByCreatedAtAsc(Long postId);

    @EntityGraph(attributePaths = {"author", "parent", "post"})
    List<Comment> findByPost_IdAndDeletedAtIsNullOrderByCreatedAtAsc(Long postId);

    @Modifying
    @Query("update Comment c set c.deletedAt = :now where c.post.id = :postId and c.deletedAt is null")
    void softDeleteAllByPostId(@Param("postId") Long postId, @Param("now") Instant now);
}
