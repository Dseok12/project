package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    /* ===== 이메일 인증 ===== */

    public record SendCodeReq(
            @Email @NotBlank String email
    ) {}

    /** 선택: 발송 성공 여부를 응답으로 받고 싶을 때 사용 */
    public record SendCodeRes(
            boolean sent
    ) {}

    public record VerifyCodeReq(
            @Email @NotBlank String email,
            @NotBlank String code
    ) {}

    /** 프론트에서 { verified: true/false } 형태로 받기 위함 */
    public record VerifyCodeRes(
            boolean verified
    ) {}

    /* ===== 회원가입 / 로그인 ===== */

    public record SignupReq(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String activityId
    ) {}

    public record LoginReq(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    /** role 포함(ADMIN 화면 가드/표시용) */
    public record LoginRes(
            String token,
            String activityId,
            String role
    ) {}

    /* ===== 활동아이디 중복 체크 ===== */

    public record ActivityIdAvailableRes(
            boolean available
    ) {}
}
