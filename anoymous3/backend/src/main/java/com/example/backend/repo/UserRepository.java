package com.example.backend.repo;

import com.example.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /* ===== Email ===== */
    // 기존 호출 호환
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // 대소문자 무시 버전 (서비스에서 소문자 정규화 안 해도 안전)
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    /* ===== ActivityId ===== */
    // 대소문자 무시 버전 (권장)
    boolean existsByActivityIdIgnoreCase(String activityId);
    Optional<User> findByActivityIdIgnoreCase(String activityId);
}
