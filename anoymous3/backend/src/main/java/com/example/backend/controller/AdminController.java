package com.example.backend.controller;

import com.example.backend.dto.AdminDtos.NoticeToggleReq;
import com.example.backend.dto.AdminDtos.SetUserRoleReq;
import com.example.backend.dto.AdminDtos.SetUserStatusReq;
import com.example.backend.dto.AdminDtos.UserAdminRes;
import com.example.backend.dto.PostDtos.CreateReq;
import com.example.backend.dto.PostDtos.PostAdminRes;
import com.example.backend.dto.PostDtos.PostRes;
import com.example.backend.dto.PostDtos.UpsertReq;
import com.example.backend.service.AdminService;
import com.example.backend.service.CommentService;
import com.example.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PostService postService;
    private final CommentService commentService;

    /* ======================= 유저 ======================= */

    /** (ADMIN) 유저 목록/검색 */
    @GetMapping("/users")
    public Page<UserAdminRes> listUsers(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminService.listUsers(q, pageable);
    }

    /** (ADMIN) 유저 상태 설정(ACTIVE/SUSPENDED/DELETED) */
    @PatchMapping("/users/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setUserStatus(@PathVariable Long id, @RequestBody @Valid SetUserStatusReq req) {
        adminService.setUserStatus(id, req);
    }

    /** (ADMIN) 유저 권한(역할) 변경: USER ↔ ADMIN */
    @PatchMapping("/users/{id}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setUserRole(@PathVariable Long id, @RequestBody @Valid SetUserRoleReq req) {
        adminService.setUserRole(id, req);
    }

    /** (ADMIN) 유저 삭제(안전-소프트 삭제 or 재지정 전략 내부 처리) */
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeleteUser(@PathVariable Long id) {
        adminService.hardDeleteUser(id);
    }

    /* ======================= 게시물/공지 ======================= */

    /** (ADMIN) 게시물 목록(삭제 포함) */
    @GetMapping("/posts")
    public Page<PostAdminRes> adminListPosts(
            @PageableDefault(size = 100, sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return postService.adminList(pageable);
    }

    /** (ADMIN) 임의의 게시글 소프트 삭제 + 댓글 일괄 소프트 삭제 */
    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAnyPost(@PathVariable Long id) {
        postService.adminDelete(id);
    }

    /** (ADMIN) 공지 on/off (req.notice == null 이면 토글) */
    @PatchMapping("/posts/{id}/notice")
    public PostRes toggleNotice(@PathVariable Long id, @RequestBody @Valid NoticeToggleReq req) {
        return postService.setNotice(id, req.notice()); // null 허용 → 서비스에서 토글 처리
    }

    /** (ADMIN) 공지 신규 작성: 일반 create 후 notice=true */
    @PostMapping("/notices")
    public PostRes createNotice(@RequestBody @Valid UpsertReq req) {
        Long id = postService.create(new CreateReq(req.title(), req.content(), Boolean.TRUE));
        return postService.setNotice(id, true);
    }

    /** (ADMIN) 공지 수정(내용 유지, notice=true 유지) */
    @PutMapping("/notices/{id}")
    public PostRes updateNotice(@PathVariable Long id, @RequestBody @Valid UpsertReq req) {
        postService.update(id, req.title(), req.content());
        return postService.setNotice(id, true);
    }

    /** (ADMIN) 공지 삭제(소프트 삭제) */
    @DeleteMapping("/notices/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotice(@PathVariable Long id) {
        postService.adminDelete(id);
    }

    /* ======================= 댓글/대댓글 ======================= */

    /** (ADMIN) 임의의 댓글/대댓글 삭제 */
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAnyComment(@PathVariable Long id) {
        commentService.adminDelete(id);
    }
}
