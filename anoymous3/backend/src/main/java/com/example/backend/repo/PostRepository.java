package com.example.backend.repo;

import com.example.backend.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** 작성자 이메일로 필터링된 게시글 페이지 */
    Page<Post> findByAuthor_Email(String email, Pageable pageable);
}
