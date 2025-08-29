package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.domain.User;
import com.example.backend.dto.PostDtos.CreatePostReq;
import com.example.backend.dto.PostDtos.PostListRes;
import com.example.backend.dto.PostDtos.PostRes;
import com.example.backend.dto.PostDtos.UpdatePostReq;
import com.example.backend.repo.PostRepository;
import com.example.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo; // 이메일 -> activityId 조회용

    /** 페이징 목록 */
    public Page<PostListRes> list(Pageable pageable) {
        return postRepo.findAll(pageable).map(this::toListRes);
    }

    /** 글 생성: 작성자 아이디 문자열 컬럼(authorActivityId)에 기록 */
    @Transactional
    public Post create(CreatePostReq req, String authorEmail) {
        String aid = null;
        if (authorEmail != null && !authorEmail.isBlank()) {
            aid = userRepo.findByEmail(authorEmail.toLowerCase())
                    .map(User::getActivityId)
                    .orElse(null);
        }
        Post p = Post.builder()
                .title(req.title())
                .content(req.content())
                .authorActivityId(aid)
                .build();
        return postRepo.save(p);
    }

    /** 엔티티 상세 */
    public Post get(Long id) {
        return postRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + id));
    }

    /** 상세 DTO */
    public PostRes getRes(Long id) {
        Post p = get(id);
        return toRes(p);
    }

    /** 글 수정 — 소유자(activityId) 검증 */
    @Transactional
    public Post update(Long id, UpdatePostReq req, String editorEmail) {
        Post p = get(id);
        String editorAid = userRepo.findByEmail(editorEmail.toLowerCase())
                .map(User::getActivityId)
                .orElse(null);

        if (editorAid == null || p.getAuthorActivityId() == null ||
                !editorAid.equals(p.getAuthorActivityId())) {
            throw new AccessDeniedException("FORBIDDEN");
        }

        p.setTitle(req.title());
        p.setContent(req.content());
        return postRepo.save(p);
    }

    /** ✅ 글 삭제 — 소유자(activityId) 검증 후 삭제 */
    @Transactional
    public void delete(Long id, String requesterEmail) {
        Post p = get(id);
        String reqAid = userRepo.findByEmail(requesterEmail.toLowerCase())
                .map(User::getActivityId)
                .orElse(null);

        if (reqAid == null || p.getAuthorActivityId() == null ||
                !reqAid.equals(p.getAuthorActivityId())) {
            throw new AccessDeniedException("FORBIDDEN");
        }

        postRepo.delete(p);
    }

    /* ===== 매핑 ===== */
    private PostListRes toListRes(Post p) {
        return new PostListRes(
                p.getId(), p.getTitle(),
                p.getAuthorActivityId(), p.getCreatedAt()
        );
    }
    private PostRes toRes(Post p) {
        return new PostRes(
                p.getId(), p.getTitle(), p.getContent(),
                p.getAuthorActivityId(), p.getCreatedAt()
        );
    }
}
