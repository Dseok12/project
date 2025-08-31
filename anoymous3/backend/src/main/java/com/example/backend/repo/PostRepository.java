// backend/src/main/java/com/example/backend/repo/PostRepository.java
package com.example.backend.repo;

import com.example.backend.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** 단건 조회 시 작성자까지 함께 로딩(N+1 예방) */
    @Override
    @EntityGraph(attributePaths = {"author"})
    Optional<Post> findById(Long id);

    /** 페이지 조회 시 작성자까지 함께 로딩 */
    @Override
    @EntityGraph(attributePaths = {"author"})
    Page<Post> findAll(Pageable pageable);

    /** 내 글만 조회 (작성자 이메일, 대소문자 무시) */
    @EntityGraph(attributePaths = {"author"})
    Page<Post> findByAuthor_EmailIgnoreCase(String email, Pageable pageable);
}
