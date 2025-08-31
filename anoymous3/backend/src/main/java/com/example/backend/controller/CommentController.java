// backend/src/main/java/com/example/backend/controller/CommentController.java
package com.example.backend.controller;

import com.example.backend.dto.CommentDtos.CommentRes;
import com.example.backend.dto.CommentDtos.CreateReq;
import com.example.backend.dto.CommentDtos.UpdateReq;
import com.example.backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public List<CommentRes> list(@PathVariable Long postId) {
        return commentService.list(postId);   // ✅ list(Long) 시그니처에 맞춤
    }

    @PostMapping("/posts/{postId}/comments")
    public CommentRes create(@PathVariable Long postId, @RequestBody @Valid CreateReq req) {
        return commentService.create(postId, req); // ✅ create(Long, CreateReq)
    }

    @PatchMapping("/comments/{id}")
    public CommentRes update(@PathVariable Long id, @RequestBody @Valid UpdateReq req) {
        return commentService.update(id, req);     // ✅ update(Long, UpdateReq)
    }

    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}
