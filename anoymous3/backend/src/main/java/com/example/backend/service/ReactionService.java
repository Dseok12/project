package com.example.backend.service;

import com.example.backend.domain.Post;
import com.example.backend.domain.PostReaction;
import com.example.backend.dto.ReactionDtos.SummaryRes;
import com.example.backend.dto.ReactionDtos.ToggleReq;
import com.example.backend.repo.PostReactionRepository;
import com.example.backend.repo.PostRepository;
import com.example.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReactionService {
    private final PostReactionRepository reactionRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public SummaryRes summary(Long postId, String email) {
        long likes = reactionRepo.countByPost_IdAndType(postId, PostReaction.Type.LIKE);
        long dislikes = reactionRepo.countByPost_IdAndType(postId, PostReaction.Type.DISLIKE);
        String my = null;
        if (email != null) {
            var user = userRepo.findByEmail(email).orElse(null);
            if (user != null) {
                var r = reactionRepo.findByPost_IdAndUser_Id(postId, user.getId()).orElse(null);
                if (r != null) my = r.getType().name();
            }
        }
        return new SummaryRes(likes, dislikes, my);
    }

    @Transactional
    public SummaryRes toggle(Long postId, String email, ToggleReq req) {
        if (email == null) throw new AccessDeniedException("로그인이 필요합니다.");
        var post = postRepo.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));
        var user = userRepo.findByEmail(email).orElseThrow();

        var existing = reactionRepo.findByPost_IdAndUser_Id(postId, user.getId());
        if (existing.isPresent()) {
            var r = existing.get();
            if (r.getType().name().equals(req.type().name())) {
                reactionRepo.delete(r); // 같은 타입 재클릭 → 취소
            } else {
                r.setType(PostReaction.Type.valueOf(req.type().name()));
            }
        } else {
            var r = PostReaction.builder()
                    .post(post).user(user)
                    .type(PostReaction.Type.valueOf(req.type().name()))
                    .build();
            reactionRepo.save(r);
        }
        return summary(postId, email);
    }
}
