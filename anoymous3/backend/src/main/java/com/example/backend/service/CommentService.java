package com.example.backend.service;

import com.example.backend.domain.Comment;
import com.example.backend.domain.Post;
import com.example.backend.domain.User;
import com.example.backend.dto.CommentDtos.CommentRes;
import com.example.backend.dto.CommentDtos.CreateReq;
import com.example.backend.dto.CommentDtos.UpdateReq;
import com.example.backend.repo.CommentRepository;
import com.example.backend.repo.PostRepository;
import com.example.backend.repo.UserRepository;
import com.example.backend.util.HtmlSanitizer;
import com.example.backend.util.MarkdownRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    /** 게시글 댓글 목록(삭제되지 않은 것만) */
    public List<CommentRes> list(Long postId) {
        return commentRepo.findByPost_IdAndDeletedAtIsNullOrderByCreatedAtAsc(postId)
                .stream().map(this::toRes).toList();
    }

    /** 댓글 작성 */
    @Transactional
    public CommentRes create(Long postId, CreateReq req) {
        String email = currentEmail();
        if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");

        User author = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("작성자 계정을 찾을 수 없습니다."));
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        Comment parent = null;
        if (req.parentId() != null) {
            parent = commentRepo.findById(req.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다."));
            if (!parent.getPost().getId().equals(postId)) {
                throw new IllegalArgumentException("부모 댓글과 게시글이 일치하지 않습니다.");
            }
        }

        // 마크다운 -> HTML -> 화이트리스트 정화
        String raw = req.content();
        String html = HtmlSanitizer.safeHtml(MarkdownRenderer.render(raw));

        Comment c = Comment.builder()
                .post(post)
                .author(author)
                .parent(parent)
                .content(html) // 정화된 HTML 저장
                .depth(parent == null ? 0 : Math.min(parent.getDepth() + 1, 3))
                .build();
        Comment saved = commentRepo.save(c);
        return toRes(saved);
    }

    /** 댓글 수정 (본인만) */
    @Transactional
    public CommentRes update(Long id, UpdateReq req) {
        String email = currentEmail();
        if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");

        Comment c = commentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
        if (c.getAuthor() == null || !email.equalsIgnoreCase(c.getAuthor().getEmail())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        String raw = req.content();
        String html = HtmlSanitizer.safeHtml(MarkdownRenderer.render(raw));

        c.setContent(html);
        return toRes(c);
    }

    /** 댓글 삭제 (본인만) — 하드 삭제 */
    @Transactional
    public void delete(Long id) {
        String email = currentEmail();
        if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");

        Comment c = commentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
        if (c.getAuthor() == null || !email.equalsIgnoreCase(c.getAuthor().getEmail())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        commentRepo.delete(c);
    }

    /** ✅ 관리자 삭제 — 소프트 삭제 */
    @Transactional
    public void adminDelete(Long id) {
        Comment c = commentRepo.findById(id).orElseThrow();
        if (c.getDeletedAt() == null) c.setDeletedAt(Instant.now());
    }

    /* ===== helpers ===== */

    private CommentRes toRes(Comment c) {
        String html = c.getContent(); // 정화된 HTML
        return new CommentRes(
                c.getId(),
                c.getPost() != null ? c.getPost().getId() : null,
                c.getParent() != null ? c.getParent().getId() : null,
                c.getAuthor() != null ? c.getAuthor().getActivityId() : null,
                null,          // contentRaw: 원문을 별도로 저장하지 않음
                html,          // contentHtml
                c.getCreatedAt()
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
