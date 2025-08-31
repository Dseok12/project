package com.example.backend.service;

import com.example.backend.domain.User;
import com.example.backend.domain.UserStatus;
import com.example.backend.dto.AdminDtos.SetUserStatusReq;
import com.example.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final UserRepository userRepo;

    public void setUserStatus(Long userId, SetUserStatusReq req) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        switch (req.status()) {
            case ACTIVE -> { u.setStatus(UserStatus.ACTIVE); u.setSuspendedUntil(null); u.setDeletedAt(null); }
            case SUSPENDED -> { u.setStatus(UserStatus.SUSPENDED); u.setSuspendedUntil(req.suspendedUntil()); u.setDeletedAt(null); }
            case DELETED -> { u.setStatus(UserStatus.DELETED); u.setSuspendedUntil(null); if (u.getDeletedAt()==null) u.setDeletedAt(Instant.now()); }
        }
        userRepo.save(u);
    }

    public void hardDeleteUser(Long userId) {
        if (userRepo.existsById(userId)) userRepo.deleteById(userId);
    }
}
