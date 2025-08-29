package com.example.backend.repo;

import com.example.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    boolean existsByActivityId(String activityId);
    Optional<User> findByEmail(String email);

    // ✅ 공백/대소문자 무시 중복 체크
    @Query("select (count(u) > 0) from User u where lower(trim(u.activityId)) = lower(trim(:aid))")
    boolean existsByActivityIdNormalized(@Param("aid") String activityId);
}
