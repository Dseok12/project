package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public void save(Post post){
        postRepository.save(post);
    }

    public void delete(Post post){
        postRepository.delete(post);
    }
}
