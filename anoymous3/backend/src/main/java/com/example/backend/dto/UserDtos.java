package com.example.backend.dto;

/** 사용자 관련 DTO 모음 */
public class UserDtos {

    /** /api/users/me 응답 */
    public record MeRes(String email, String activityId) {}

    /** /api/users/me/activity-logs 응답(프론트 404 방지용, 일단 비워서 내려도 됨) */
    public record ActivityLogRes(Long id, String action, String createdAt) {}

    /** /api/users/me (PUT) 요청 바디 — 현재 intro 필드는 엔티티에 없지만, 프론트 PUT을 204로만 응답해 주기 위해 둠 */
    public record UpdateIntroReq(String intro) {}
}
