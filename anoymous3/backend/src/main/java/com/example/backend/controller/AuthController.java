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

    // 활동 아이디 중복 체크
    @GetMapping("/activity-id/available")
    public Map<String, Boolean> available(@RequestParam String activityId) {
        boolean ok = auth.availableActivityId(activityId);
        return Map.of("available", ok);
    }

    // 이메일 코드 발송(개발 모드면 no-op)
    @PostMapping("/send-code")
    public void sendCode(@RequestBody @Valid SendCodeReq req) {
        auth.sendCode(req);
    }

    // 코드 검증(개발 모드면 항상 false/미사용)
    @PostMapping("/verify-code")
    public Map<String, Boolean> verify(@RequestBody @Valid VerifyCodeReq req) {
        return Map.of("verified", auth.verifyCode(req));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupReq req) {
        auth.signup(req);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // 로그인
    @PostMapping("/login")
    public LoginRes login(@RequestBody @Valid LoginReq req) {
        return auth.login(req);
    }
}
