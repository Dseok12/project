package com.example.backend.controller;

import com.example.backend.dto.AdminDtos.NoticeToggleReq;
import com.example.backend.dto.AdminDtos.SetUserStatusReq;
import com.example.backend.dto.PostDtos.PostRes;
import com.example.backend.service.AdminService;
import com.example.backend.service.CommentService;
import com.example.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PostService postService;
    private final CommentService commentService;

    // --- 게시물 ---
    @DeleteMapping("/posts/{id}")
    public void deleteAnyPost(@PathVariable Long id) { postService.adminDelete(id); }

    @PatchMapping("/posts/{id}/notice")
    public PostRes toggleNotice(@PathVariable Long id, @RequestBody @Valid NoticeToggleReq req) {
        return postService.setNotice(id, req.notice());
    }

    // --- 댓글/대댓글 ---
    @DeleteMapping("/comments/{id}")
    public void deleteAnyComment(@PathVariable Long id) { commentService.adminDelete(id); }

    // --- 유저 ---
    @PatchMapping("/users/{id}/status")
    public void setUserStatus(@PathVariable Long id, @RequestBody @Valid SetUserStatusReq req) {
        adminService.setUserStatus(id, req);
    }

    @DeleteMapping("/users/{id}")
    public void hardDeleteUser(@PathVariable Long id) { adminService.hardDeleteUser(id); }
}
