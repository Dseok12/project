package com.example.backend.controller;

import com.example.backend.domain.Post;
import com.example.backend.dto.PostDtos.CreatePostReq;
import com.example.backend.dto.PostDtos.PostListRes;
import com.example.backend.dto.PostDtos.PostRes;
import com.example.backend.dto.PostDtos.UpdatePostReq;
import com.example.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService svc;

    /** 최신순 20개 페이징 목록 */
    @GetMapping
    public Page<PostListRes> list(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return svc.list(pageable);
    }

    /** 상세 */
    @GetMapping("/{id}")
    public PostRes get(@PathVariable Long id) {
        try { return svc.getRes(id); }
        catch (NoSuchElementException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"); }
    }

    /** 생성(인증 필요) */
    @PostMapping
    public PostRes create(@RequestBody @Valid CreatePostReq req,
                          @AuthenticationPrincipal String email) {
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        Post p = svc.create(req, email);
        return new PostRes(p.getId(), p.getTitle(), p.getContent(), p.getAuthorActivityId(), p.getCreatedAt());
    }

    /** 수정(인증 + 소유자만) */
    @PutMapping("/{id}")
    public PostRes update(@PathVariable Long id,
                          @RequestBody @Valid UpdatePostReq req,
                          @AuthenticationPrincipal String email) {
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        try {
            Post p = svc.update(id, req, email);
            return new PostRes(p.getId(), p.getTitle(), p.getContent(), p.getAuthorActivityId(), p.getCreatedAt());
        } catch (AccessDeniedException ade) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
        } catch (NoSuchElementException nse) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }

    /** ✅ 삭제(인증 + 소유자만) — 204 No Content */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal String email) {
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        try {
            svc.delete(id, email);
        } catch (AccessDeniedException ade) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
        } catch (NoSuchElementException nse) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }
}
