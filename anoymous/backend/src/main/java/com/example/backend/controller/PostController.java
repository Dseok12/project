package com.example.backend.controller;

import com.example.backend.domain.Post;
import com.example.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

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

        postService.save(post);
        return ResponseEntity.ok("게시물 작성 완료");
    }

    @GetMapping("/posts")
    public Page<Post> getPosts(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "id"));
        return postService.findAll(pageable);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        return postService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String author,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");

        Post post = optionalPost.get();
        if (!post.getAuthor().equals(author))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 수정할 수 있습니다.");

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

        postService.save(post);
        return ResponseEntity.ok("수정 완료");
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            @RequestParam String author
    ) {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");

        Post post = optionalPost.get();
        if (!post.getAuthor().equals(author))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 삭제할 수 있습니다.");

        postService.delete(post);
        return ResponseEntity.ok("삭제 완료");
    }
}
