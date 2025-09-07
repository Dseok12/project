package com.example.backend.controller;

import com.example.backend.dto.PostDtos.CreateReq;
import com.example.backend.dto.PostDtos.PostRes;
import com.example.backend.dto.PostDtos.UpsertReq;
import com.example.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** 단건 조회(삭제된 글은 400) */
    @GetMapping("/{id}")
    public PostRes get(@PathVariable Long id) {
        return postService.get(id);
    }

    /**
     * 목록 조회
     * - ?mine=true   : 내 글만 (로그인 필요)
     * - ?notice=true : 공지만 (누구나 읽기)
     * - 기본         : 삭제되지 않은 전체 글
     * 정렬은 createdAt,desc 기본.
     */
    @GetMapping
    public Page<PostRes> list(
            @RequestParam(required = false) Boolean mine,
            @RequestParam(required = false) Boolean notice,
            @PageableDefault(size = 20, sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return postService.list(mine, notice, pageable);
    }

    /** 편의: /api/posts/notices -> 공지 전용 목록(읽기 전용) */
    @GetMapping("/notices")
    public Page<PostRes> listNotices(
            @PageableDefault(size = 20, sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return postService.list(null, true, pageable);
    }

    /** 게시글 작성
     *  - 일반 유저: notice 값은 무시되어 항상 false
     *  - 관리자   : notice=true 가능
     */
    @PostMapping
    public IdRes create(@RequestBody @Valid CreateReq req) {
        Long id = postService.create(req);
        return new IdRes(id);
    }

    /** 게시글 수정(본인만) – 공지 여부는 AdminController에서 따로 처리 */
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody @Valid UpsertReq req) {
        postService.update(id, req.title(), req.content());
    }

    /** 게시글 삭제(본인만) – 서비스에서 소프트 삭제 처리 */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }

    public record IdRes(Long id) {}
}
