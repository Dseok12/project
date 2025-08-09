package com.example.backend.controller;

import com.example.backend.domain.Comment;
import com.example.backend.domain.Post;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String author,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);

        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String savePath = "uploads/" + fileName;
            File dest = new File(savePath);
            dest.getParentFile().mkdirs();
            image.transferTo(dest);
            post.setImageUrl("/uploads/" + fileName);
        }
        postRepository.save(post);
        return ResponseEntity.ok("게시글 등록 완료");
    }

    @GetMapping("/posts")
    public Page<Post> list(@RequestParam(defaultValue="0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"id"));
        return postRepository.findAll(pageable);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setContent(content);
        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String savePath = "uploads/" + fileName;
            File dest = new File(savePath);
            dest.getParentFile().mkdirs();
            image.transferTo(dest);
            post.setImageUrl("/uploads/" + fileName);
        }
        postRepository.save(post);
        return ResponseEntity.ok("수정 완료");
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.ok("삭제 완료");
    }

    // 댓글 추가
    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long id,
            @RequestParam String commentWriter,
            @RequestParam String commentContents
    ) {
        Post post = postRepository.findById(id).orElseThrow();
        Comment c = new Comment();
        c.setPost(post);
        c.setCommentWriter(commentWriter);
        c.setCommentContents(commentContents);
        commentRepository.save(c);
        return ResponseEntity.ok("댓글 등록 완료");
    }

    @GetMapping("/posts/{id}/comments")
    public List<Comment> getComments(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        return commentRepository.findByPost(post);
    }
}
