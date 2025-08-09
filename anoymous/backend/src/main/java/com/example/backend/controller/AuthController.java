package com.example.backend.controller;

import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일입니다.");
        }
        userRepository.save(user);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        Optional<User> userOpt = userRepository.findByEmail(body.get("email"));
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(body.get("password"))) {
            User user = userOpt.get();
            // 실서비스는 JWT 발급
            return ResponseEntity.ok(Map.of(
                    "token", user.getEmail(),  // 여기선 단순 email을 토큰처럼 사용
                    "username", user.getUsername()
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
    }
}
