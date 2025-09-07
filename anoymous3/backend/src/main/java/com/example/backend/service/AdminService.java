package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.domain.Role;
import com.example.backend.domain.User;
import com.example.backend.domain.UserStatus;
import com.example.backend.dto.AdminDtos.SetUserRoleReq;
import com.example.backend.dto.AdminDtos.SetUserStatusReq;
import com.example.backend.dto.AdminDtos.UserAdminRes;
import com.example.backend.repo.CommentRepository;
import com.example.backend.repo.PostRepository;
import com.example.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;

    /* ================= 유저 목록/검색 ================= */

    @Transactional(readOnly = true)
    public Page<UserAdminRes> listUsers(String q, Pageable pageable) {
        if (q != null && !q.isBlank()) {
            return userRepo
                    .findByActivityIdContainingIgnoreCaseOrEmailContainingIgnoreCase(q.trim(), q.trim(), pageable)
                    .map(this::toUserAdminRes);
        }
        return userRepo.findAll(pageable).map(this::toUserAdminRes);
    }

    private UserAdminRes toUserAdminRes(User u) {
        return new UserAdminRes(
                u.getId(),
                u.getEmail(),
                u.getActivityId(),
                u.getStatus(),
                u.getRole(),
                u.getSuspendedUntil()
        );
    }

    /* ================= 유저 상태/권한 ================= */

    /** 관리자: 사용자 상태 설정 */
    public void setUserStatus(Long userId, SetUserStatusReq req) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (req.status() == null) throw new IllegalArgumentException("status는 필수입니다.");

        switch (req.status()) {
            case ACTIVE -> {
                u.setStatus(UserStatus.ACTIVE);
                u.setSuspendedUntil(null);
                u.setDeletedAt(null);
            }
            case SUSPENDED -> {
                u.setStatus(UserStatus.SUSPENDED);
                // 기간 없으면 null(무기한 정지)
                u.setSuspendedUntil(req.suspendedUntil());
                u.setDeletedAt(null);
            }
            case DELETED -> {
                u.setStatus(UserStatus.DELETED);
                u.setSuspendedUntil(null);
                if (u.getDeletedAt() == null) u.setDeletedAt(Instant.now());
            }
        }
        userRepo.save(u);
    }

    /** 관리자: 권한(역할) 변경 - USER ↔ ADMIN */
    public void setUserRole(Long userId, SetUserRoleReq req) {
        if (req.role() == null) throw new IllegalArgumentException("role은 필수입니다.");
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        u.setRole(req.role());
        userRepo.save(u);
    }

    /* ================= 안전 삭제(소프트 삭제) =================
       실제 하드 딜리트는 FK 제약(게시글/댓글) 때문에 실패합니다.
       여기서는
        1) 유저 상태를 DELETED로 바꾸고 삭제 시각을 기록
        2) 해당 유저의 모든 게시글을 소프트 삭제(삭제 시각 설정)
        3) 해당 게시글의 댓글도 소프트 삭제
       로 처리해 1451 제약 오류를 방지합니다.
    */
    public void hardDeleteUser(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Instant now = Instant.now();

        // 1) 유저 소프트 삭제 표시
        u.setStatus(UserStatus.DELETED);
        u.setSuspendedUntil(null);
        if (u.getDeletedAt() == null) u.setDeletedAt(now);
        userRepo.save(u);

        // 2) 해당 유저의 게시글 소프트 삭제 + 3) 댓글 소프트 삭제
        List<Post> posts = postRepo.findByAuthor_Id(userId);
        for (Post p : posts) {
            if (p.getDeletedAt() == null) {
                p.setDeletedAt(now);
            }
            commentRepo.softDeleteAllByPostId(p.getId(), now);
        }
        // flush는 트랜잭션 끝에서 자동
    }
}
