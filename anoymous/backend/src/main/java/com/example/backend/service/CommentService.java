package com.example.backend.service;

import com.example.backend.domain.Comment;
import com.example.backend.domain.Post;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<Comment> findByPostId(Long postId){
        Post post = postRepository.findById(postId).orElseThrow();
        return commentRepository.findByPost(post);
    }

    public Comment addComment(Long postId, String content, String writer){
        Post post = postRepository.findById(postId).orElseThrow();
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setCommentContents(content);
        comment.setCommentWriter(writer);
        return commentRepository.save(comment);
    }
}
