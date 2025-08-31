// backend/src/main/java/com/example/backend/service/AuthService.java
package com.example.backend.service;

import com.example.backend.domain.EmailVerification;
import com.example.backend.domain.User;
import com.example.backend.dto.AuthDtos.*;
import com.example.backend.repo.EmailVerificationRepository;
import com.example.backend.repo.UserRepository;
import com.example.backend.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository evRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwt;

    /** 이메일 인증 사용 여부 (개발 중 꺼두기 가능) */
    @Value("${feature.email-verification.enabled:true}")
    private boolean emailVerificationEnabled;

    /** 메일 발신자 주소(선택) */
    @Value("${spring.mail.from:}")
    private String mailFrom;

    private String gen6() {
        SecureRandom r = new SecureRandom();
        int n = r.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    @Transactional
    public void sendCode(SendCodeReq req) {
        // 이메일 인증 비활성화 시 바로 리턴(개발 모드)
        if (!emailVerificationEnabled) return;

        final String email = normalizeEmail(req.email());
        String code = gen6();
        Instant expires = Instant.now().plusSeconds(3600);

        EmailVerification ev = evRepo.findByEmail(email)
                .orElse(EmailVerification.builder().email(email).build());
        ev.setCode(code);
        ev.setExpiresAt(expires);
        ev.setVerifiedAt(null);
        evRepo.save(ev);

        SimpleMailMessage msg = new SimpleMailMessage();
        if (mailFrom != null && !mailFrom.isBlank()) {
            msg.setFrom(mailFrom);
        }
        msg.setTo(email);
        msg.setSubject("[어노이] 이메일 인증 코드");
        msg.setText("인증 코드: " + code + "\n유효기간: 1시간");
        mailSender.send(msg);
    }

    @Transactional
    public boolean verifyCode(VerifyCodeReq req) {
        final String email = normalizeEmail(req.email());
        final String code = safeTrim(req.code());

        return evRepo.findByEmail(email).map(ev -> {
            boolean ok = code != null
                    && code.equals(ev.getCode())
                    && ev.getExpiresAt().isAfter(Instant.now());
            if (ok) {
                ev.setVerifiedAt(Instant.now()); // 성공 시각 기록
                evRepo.save(ev);
            }
            return ok;
        }).orElse(false);
    }

    @Transactional
    public void signup(SignupReq req) {
        final String email = normalizeEmail(req.email());
        final String rawActivityId = safeTrim(req.activityId());
        final String normActivityId = normalizeActivityId(rawActivityId);

        if (email == null || normActivityId == null || safeTrim(req.password()) == null) {
            throw new IllegalArgumentException("입력값을 확인하세요.");
        }

        if (userRepo.existsByEmail(email)) throw new IllegalArgumentException("이미 등록된 이메일");
        // 정규화 기준으로 중복 체크
        if (activityIdExistsNormalized(normActivityId))
            throw new IllegalArgumentException("이미 사용 중인 활동아이디");

        if (emailVerificationEnabled) {
            EmailVerification ev = evRepo.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("이메일 인증 필요"));
            if (ev.getVerifiedAt() == null || ev.getVerifiedAt().isBefore(Instant.now().minusSeconds(3600))) {
                throw new IllegalStateException("이메일 인증 만료");
            }
            // 인증 레코드 소모(선택)
            evRepo.delete(ev);
        }

        String hash = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        User u = User.builder()
                .email(email)
                .passwordHash(hash)
                // 저장 시에도 정규화 적용
                .activityId(normActivityId)
                .build();
        userRepo.save(u);
    }

    public LoginRes login(LoginReq req) {
        final String email = normalizeEmail(req.email());
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("계정 없음"));

        if (!BCrypt.checkpw(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        // JWT에 role/aid를 넣어 발급
        String roleClaim = (u.getRole() != null) ? u.getRole().name() : "USER";
        String token = jwt.createToken(
                u.getEmail(),
                Map.of("activityId", u.getActivityId(), "role", roleClaim)
        );

        // LoginRes가 (token, activityId, role) 시그니처일 때
        return new LoginRes(token, u.getActivityId(), roleClaim);
    }

    public boolean availableActivityId(String activityId) {
        // 정규화 후 빈값이면 false, 그 외엔 정규화 기준으로 조회
        String norm = normalizeActivityId(activityId);
        if (norm == null) return false;
        return !activityIdExistsNormalized(norm);
    }

    /* ===== helpers ===== */

    private static String safeTrim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String normalizeEmail(String email) {
        String t = safeTrim(email);
        return t == null ? null : t.toLowerCase();
    }

    private static String normalizeActivityId(String s) {
        String t = safeTrim(s);
        return t == null ? null : t.toLowerCase();
    }

    /**
     * Repository를 바꾸지 않고 서비스 단에서 정규화 비교.
     * (개발 단계용 간단 구현 — 운영에선 정규화 인덱스/쿼리로 대체 권장)
     */
    private boolean activityIdExistsNormalized(String normActivityId) {
        return userRepo.findAll().stream()
                .map(User::getActivityId)
                .map(AuthService::normalizeActivityId)
                .anyMatch(normActivityId::equals);
    }
}
