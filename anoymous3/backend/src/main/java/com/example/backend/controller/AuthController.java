package com.example.backend.controller;

import com.example.backend.service.AuthService;
import com.example.backend.dto.AuthDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth") // ✅ 반드시 /api 붙이기
public class AuthController {

    private final AuthService auth;

    @GetMapping("/activity-id/available")
    public Map<String, Boolean> available(@RequestParam String activityId) {
        return Map.of("available", auth.availableActivityId(activityId));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupReq req) {
        auth.signup(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@RequestBody SendCodeReq req) {
        auth.sendCode(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public Map<String, Boolean> verify(@RequestBody VerifyCodeReq req) {
        return Map.of("verified", auth.verifyCode(req));
    }

    @PostMapping("/login")
    public LoginRes login(@RequestBody LoginReq req) {
        return auth.login(req);
    }
}
