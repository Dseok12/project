package com.example.backend.dto;

import java.time.Instant;

public class PostDtos {

    // 게시글 조회 응답
    public record PostRes(
            Long id,
            String title,
            String content,
            String authorActivityId,
            Instant createdAt
    ) {}

    // 생성 요청
    public record CreateReq(
            String title,
            String content
    ) {}

    // 수정 요청 (PUT/PATCH 공용)
    public record UpsertReq(
            String title,
            String content
    ) {}
}
