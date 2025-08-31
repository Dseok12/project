package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.domain.User;
import com.example.backend.dto.PostDtos.CreateReq;
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

    public Page<PostRes> list(Boolean mine, Pageable pageable) {
        if (Boolean.TRUE.equals(mine)) {
            String email = currentEmail();
            if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");
            return postRepo.findByAuthor_EmailIgnoreCase(email, pageable)
                    .map(this::toRes);
        }
        return postRepo.findAll(pageable).map(this::toRes);
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
                .notice(false)
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

    // ===== ✅ 관리자용 기능 =====

    /** 공지 on/off */
    @Transactional
    public PostRes setNotice(Long id, boolean notice) {
        Post p = postRepo.findById(id).orElseThrow();
        p.setNotice(notice);
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

    // ===== helpers =====

    private PostRes toRes(Post p) {
        return new PostRes(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getAuthor() != null ? p.getAuthor().getActivityId() : null,
                p.getCreatedAt()
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
