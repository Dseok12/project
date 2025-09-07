package com.example.backend.repo;

import com.example.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /* ===== Email ===== */
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // 대소문자 무시 버전
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    /* ===== ActivityId ===== */
    // 대소문자 무시 버전 (권장)
    boolean existsByActivityIdIgnoreCase(String activityId);
    Optional<User> findByActivityIdIgnoreCase(String activityId);

    /* ===== 관리자: 목록/검색 ===== */
    Page<User> findByActivityIdContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String activityId, String email, Pageable pageable
    );
}
