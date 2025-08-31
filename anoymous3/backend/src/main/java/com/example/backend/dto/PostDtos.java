package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class PostDtos {

    public record PostRes(
            Long id,
            String title,
            String content,
            String authorActivityId,
            Instant createdAt
    ) {}

    public record CreateReq(
            @NotBlank
            @Size(max = 255)            // 제목은 255 예시 (DB 길이에 맞추세요)
            String title,

            @Size(max = 999_999_999)    // ✅ 본문 최대 999,999,999자
            String content
    ) {}

    public record UpsertReq(
            @NotBlank
            @Size(max = 255)
            String title,

            @Size(max = 999_999_999)    // ✅ 동일하게 적용
            String content
    ) {}
}
