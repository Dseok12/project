// backend/src/main/java/com/example/backend/repo/PostRepository.java
package com.example.backend.repo;

import com.example.backend.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /* ===== 삭제되지 않은 글 기본 목록 ===== */
    @EntityGraph(attributePaths = {"author"})
    Page<Post> findByDeletedAtIsNull(Pageable pageable);

    /** 내 글(삭제되지 않은 것만) */
    @EntityGraph(attributePaths = {"author"})
    Page<Post> findByDeletedAtIsNullAndAuthor_EmailIgnoreCase(String email, Pageable pageable);

    /* ===== 공지/일반 전용 목록(삭제되지 않은 것만) ===== */
    @EntityGraph(attributePaths = {"author"})
    Page<Post> findByNoticeTrueAndDeletedAtIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    Page<Post> findByNoticeFalseAndDeletedAtIsNull(Pageable pageable);

    /* ===== (옵션) 통계 ===== */
    long countByNoticeTrue();

    /* ===== 검색(제목/본문) + 공지 필터 + 삭제 제외 =====
       ⚠ LOWER() 제거: p.content 가 CLOB(@Lob)이라 Hibernate 6에서 LOWER 인자로 허용되지 않음
     */
    @EntityGraph(attributePaths = {"author"})
    @Query("""
           SELECT p FROM Post p
           WHERE p.deletedAt IS NULL
             AND (:notice IS NULL OR p.notice = :notice)
             AND (
               :q IS NULL
               OR p.title   LIKE CONCAT('%', :q, '%')
               OR p.content LIKE CONCAT('%', :q, '%')
             )
           """)
    Page<Post> search(@Param("q") String q,
                      @Param("notice") Boolean notice,
                      Pageable pageable);

    /* ===== (옵션) 관리자용: 삭제 포함 조회 ===== */
    @Query(value = "SELECT * FROM posts ORDER BY created_at DESC",
            countQuery = "SELECT count(*) FROM posts",
            nativeQuery = true)
    Page<Post> findAllIncludingDeleted(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE notice = true ORDER BY created_at DESC",
            countQuery = "SELECT count(*) FROM posts WHERE notice = true",
            nativeQuery = true)
    Page<Post> findNoticesIncludingDeleted(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE id = :id", nativeQuery = true)
    Optional<Post> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM posts WHERE author_id = :authorId ORDER BY created_at DESC",
            countQuery = "SELECT count(*) FROM posts WHERE author_id = :authorId",
            nativeQuery = true)
    Page<Post> findByAuthorIdIncludingDeleted(@Param("authorId") Long authorId, Pageable pageable);

    /** 특정 작성자의 글(삭제 포함/미포함 무관) 전체 조회 */
    java.util.List<Post> findByAuthor_Id(Long authorId);
}
