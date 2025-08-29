package com.example.backend.controller;

import com.example.backend.dto.PostDtos.CreateReq;
import com.example.backend.dto.PostDtos.PostRes;
import com.example.backend.dto.PostDtos.UpsertReq;
import com.example.backend.service.PostService;
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

    @GetMapping("/{id}")
    public PostRes get(@PathVariable Long id) {
        return postService.get(id);
    }

    // 프론트에서 ?mine=true&sort=createdAt,desc 로 호출
    @GetMapping
    public Page<PostRes> list(
            @RequestParam(required = false) Boolean mine,
            @PageableDefault(size = 20, sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return postService.list(mine, pageable);
    }

    @PostMapping
    public IdRes create(@RequestBody CreateReq req) {
        Long id = postService.create(req);
        return new IdRes(id);
    }

    /** 게시글 수정 (프론트에서 PUT 사용) */
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody UpsertReq req) {
        postService.update(id, req.title(), req.content());
    }

    /** 게시글 삭제 (프론트에서 DELETE 사용) */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }

    public record IdRes(Long id) {}
}
