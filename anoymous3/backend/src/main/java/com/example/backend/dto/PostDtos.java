package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class PostDtos {

    /** 기본 응답 DTO (클라이언트 일반 화면용) */
    public record PostRes(
            Long id,
            String title,
            String content,
            String authorActivityId,
            Instant createdAt
    ) {}

    /**
     * 관리자/공지 전용 확장 응답 DTO.
     * 공지 여부, 업데이트/삭제 시각까지 포함이 필요할 때 선택적으로 사용하세요.
     */
    public record PostAdminRes(
            Long id,
            String title,
            String content,
            String authorActivityId,
            boolean notice,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {}

    /**
     * 생성 요청 DTO.
     * - 일반 사용자가 notice 값을 보내도 서버에서 무시(권한 체크로 false 강제)
     * - 관리자는 notice=true 로 공지 생성 가능
     */
    public record CreateReq(
            @NotBlank
            @Size(max = 255)
            String title,

            @Size(max = 999_999_999)
            String content,

            /** 관리자만 의미 있는 값(서비스에서 ROLE_ADMIN만 반영) */
            Boolean notice
    ) {}

    /**
     * 수정/업서트 요청 DTO.
     * - 일반 사용자는 notice 무시
     * - 관리자는 notice 변경 가능
     */
    public record UpsertReq(
            @NotBlank
            @Size(max = 255)
            String title,

            @Size(max = 999_999_999)
            String content,

            /** 관리자만 의미 있는 값(서비스에서 ROLE_ADMIN만 반영) */
            Boolean notice
    ) {}
}
