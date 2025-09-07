package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.domain.User;
import com.example.backend.dto.PostDtos.CreateReq;
import com.example.backend.dto.PostDtos.PostAdminRes;
import com.example.backend.dto.PostDtos.PostRes;
import com.example.backend.repo.CommentRepository;
import com.example.backend.repo.PostRepository;
import com.example.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;

    public PostRes get(Long id) {
        Post p = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));
        if (p.getDeletedAt() != null) throw new IllegalArgumentException("삭제된 게시물입니다.");
        return toRes(p);
    }

    /** 기존 시그니처 유지(컨트롤러 호환) */
    public Page<PostRes> list(Boolean mine, Pageable pageable) {
        return list(mine, null, pageable);
    }

    /** notice 필터까지 지원하는 확장 목록 API */
    public Page<PostRes> list(Boolean mine, Boolean notice, Pageable pageable) {
        // 공지/일반 필터
        if (notice != null) {
            if (Boolean.TRUE.equals(notice)) {
                return postRepo.findByNoticeTrueAndDeletedAtIsNull(pageable).map(this::toRes);
            } else {
                return postRepo.findByNoticeFalseAndDeletedAtIsNull(pageable).map(this::toRes);
            }
        }

        // 내 글만
        if (Boolean.TRUE.equals(mine)) {
            String email = currentEmail();
            if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");
            return postRepo.findByDeletedAtIsNullAndAuthor_EmailIgnoreCase(email, pageable)
                    .map(this::toRes);
        }

        // 전체(삭제되지 않은 글만)
        return postRepo.findByDeletedAtIsNull(pageable).map(this::toRes);
    }

    /** (ADMIN) 게시물 목록 — 삭제 제외(안정 버전) */
    public Page<PostAdminRes> adminList(Pageable pageable) {
        // 기존 native(삭제 포함) 경로의 500을 피하기 위해 표준 JPA 경로 사용
        // 필요 시 삭제 포함 버전은 별도 native + projection으로 재도입 가능
        return postRepo.findAll(pageable).map(this::toAdminRes);
    }

    @Transactional
    public Long create(CreateReq req) {
        String email = currentEmail();
        if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");

        User author = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("작성자 계정을 찾을 수 없습니다."));

        Post p = Post.builder()
                .title(req.title())
                .content(req.content())
                .author(author)
                .notice(false) // 일반 작성은 기본 false (공지 작성은 /api/admin/notices 사용)
                .build();
        Post saved = postRepo.save(p);
        return saved.getId();
    }

    @Transactional
    public void update(Long id, String title, String content) {
        String email = currentEmail();
        if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");

        Post p = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        if (p.getAuthor() == null || !email.equalsIgnoreCase(p.getAuthor().getEmail())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        if (title != null && !title.isBlank()) p.setTitle(title.trim());
        if (content != null) p.setContent(content);
    }

    @Transactional
    public void delete(Long id) {
        String email = currentEmail();
        if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");

        Post p = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        if (p.getAuthor() == null || !email.equalsIgnoreCase(p.getAuthor().getEmail())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        postRepo.delete(p);
    }

    /* ===== 관리자용 ===== */

    /**
     * 공지 on/off
     * - notice == null 이면 현재 값 반대로 토글
     */
    @Transactional
    public PostRes setNotice(Long id, Boolean notice) {
        Post p = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));
        if (notice == null) {
            p.setNotice(!p.isNotice());
        } else {
            p.setNotice(notice);
        }
        return toRes(p);
    }

    /** 게시글 소프트 삭제 + 댓글 일괄 소프트 삭제 */
    @Transactional
    public void adminDelete(Long id) {
        Post p = postRepo.findById(id).orElseThrow();
        if (p.getDeletedAt() == null) {
            p.setDeletedAt(Instant.now());
        }
        commentRepo.softDeleteAllByPostId(id, Instant.now());
    }

    /* ===== helpers ===== */

    private PostRes toRes(Post p) {
        return new PostRes(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getAuthor() != null ? p.getAuthor().getActivityId() : null,
                p.getCreatedAt()
        );
    }

    private PostAdminRes toAdminRes(Post p) {
        return new PostAdminRes(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getAuthor() != null ? p.getAuthor().getActivityId() : null,
                p.isNotice(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getDeletedAt()
        );
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) return ud.getUsername();
        String name = auth.getName();
        return (name != null && !name.isBlank()) ? name : null;
    }
}
