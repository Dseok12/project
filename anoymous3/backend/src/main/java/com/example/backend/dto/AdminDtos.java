package com.example.backend.dto;

import com.example.backend.domain.Role;
import com.example.backend.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class AdminDtos {
    /** 공지 토글 요청 */
    public record NoticeToggleReq(Boolean notice) {}

    /** 유저 상태 변경 요청 (정지 해제는 ACTIVE, 영구 정지는 SUSPENDED + suspendedUntil=null) */
    public record SetUserStatusReq(@NotNull UserStatus status, Instant suspendedUntil) {}

    /** 유저 역할(권한) 변경 요청: USER ↔ ADMIN */
    public record SetUserRoleReq(@NotNull Role role) {}

    /** 관리자 유저 목록 응답 DTO */
    public record UserAdminRes(
            Long id,
            String email,
            String activityId,
            UserStatus status,
            Role role,
            Instant suspendedUntil
    ) {}
}
