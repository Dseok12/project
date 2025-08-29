package com.example.backend.controller;

import com.example.backend.domain.User;
import com.example.backend.dto.UserDtos.MeRes;
import com.example.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class UserMeController {

    private final UserRepository userRepo;

    // 내 정보 조회 (GET)
    @GetMapping
    public MeRes me(Authentication auth) {
        User u = requireMe(auth);
        return new MeRes(u.getEmail(), u.getActivityId());
    }

    // 내 activityId 변경 (PUT)
    @PutMapping
    public ResponseEntity<MeRes> update(Authentication auth, @RequestBody UpdateMeReq req) {
        User u = requireMe(auth);

        if (req.activityId() != null && !req.activityId().isBlank()) {
            String norm = req.activityId().trim().toLowerCase();
            // 리포지토리에 있는 IgnoreCase 버전 사용
            if (!norm.equalsIgnoreCase(u.getActivityId())
                    && userRepo.existsByActivityIdIgnoreCase(norm)) {
                throw new IllegalArgumentException("이미 사용 중인 활동아이디");
            }
            u.setActivityId(norm);
            userRepo.save(u);
        }

        return ResponseEntity.ok(new MeRes(u.getEmail(), u.getActivityId()));
    }

    // 프론트에서 호출하는 활동 로그 (없으면 404 나므로 빈 리스트라도 반환)
    @GetMapping("/activity-logs")
    public List<Map<String, Object>> activityLogs() {
        return List.of(); // TODO: 실제 데이터로 교체
    }

    private User requireMe(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        return userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public record UpdateMeReq(String activityId) {}
}
