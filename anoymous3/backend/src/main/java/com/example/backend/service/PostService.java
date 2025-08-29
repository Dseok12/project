package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.domain.User;
import com.example.backend.dto.PostDtos.CreateReq;
import com.example.backend.dto.PostDtos.PostRes;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public PostRes get(Long id) {
        Post p = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));
        return toRes(p);
    }

    public Page<PostRes> list(Boolean mine, Pageable pageable) {
        if (Boolean.TRUE.equals(mine)) {
            String email = currentEmail();
            if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");
            return postRepo.findByAuthor_EmailIgnoreCase(email, pageable).map(this::toRes);
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
        // 트랜잭션 종료 시 flush
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
