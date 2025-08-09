package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.domain.PostLike;
import com.example.backend.repository.LikeRepository;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public String toggleLike(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow();
        return likeRepository.findByPostAndUsername(post, username)
                .map(like -> {
                    likeRepository.delete(like);
                    return "좋아요 취소";
                })
                .orElseGet(() -> {
                    PostLike newLike = new PostLike();
                    newLike.setPost(post);
                    newLike.setUsername(username);
                    likeRepository.save(newLike);
                    return "좋아요 성공";
                });
    }
}
