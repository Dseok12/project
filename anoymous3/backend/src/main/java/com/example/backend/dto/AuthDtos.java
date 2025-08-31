package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record SendCodeReq(@Email @NotBlank String email) {}
    public record VerifyCodeReq(@Email @NotBlank String email, @NotBlank String code) {}
    public record VerifyCodeRes(boolean verified) {}

    public record SignupReq(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String activityId
    ) {}

    public record LoginReq(@Email @NotBlank String email, @NotBlank String password) {}

    // ✅ role 포함(ADMIN 화면 가드용)
    public record LoginRes(String token, String activityId, String role) {}

    public record ActivityIdAvailableRes(boolean available) {}
}
