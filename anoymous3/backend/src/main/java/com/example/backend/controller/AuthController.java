package com.example.backend.controller;

import com.example.backend.dto.AuthDtos.*;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    /** 활동 아이디 사용 가능 여부 확인 */
    @GetMapping("/activity-id/available")
    public Map<String, Boolean> available(@RequestParam String activityId) {
        boolean ok = auth.availableActivityId(activityId);
        return Map.of("available", ok);
    }

    /** 이메일 인증 코드 발송 (개발 모드에선 no-op) */
    @PostMapping("/send-code")
    public void sendCode(@RequestBody @Valid SendCodeReq req) {
        auth.sendCode(req);
    }

    /** 인증 코드 검증 (개발 모드면 미사용/false 일 수 있음) */
    @PostMapping("/verify-code")
    public Map<String, Boolean> verify(@RequestBody @Valid VerifyCodeReq req) {
        return Map.of("verified", auth.verifyCode(req));
    }

    /** 회원가입 */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupReq req) {
        auth.signup(req);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** 로그인 */
    @PostMapping("/login")
    public LoginRes login(@RequestBody @Valid LoginReq req) {
        return auth.login(req);
    }
}
