package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class PostDtos {

    public record CreatePostReq(
            @NotBlank String title,
            @NotBlank String content
    ) {}

    // ✅ 수정용 DTO 추가
    public record UpdatePostReq(
            @NotBlank String title,
            @NotBlank String content
    ) {}

    // 상세 응답(작성자 포함)
    public record PostRes(
            Long id,
            String title,
            String content,
            String authorActivityId,
            Instant createdAt
    ) {}

    // 목록 응답(요약)
    public record PostListRes(
            Long id,
            String title,
            String authorActivityId,
            Instant createdAt
    ) {}
}
