package com.example.backend.controller;

import com.example.backend.dto.ReactionDtos.SummaryRes;
import com.example.backend.dto.ReactionDtos.ToggleReq;
import com.example.backend.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/reactions")
public class ReactionController {
    private final ReactionService reactionService;

    @GetMapping
    public SummaryRes summary(@PathVariable Long postId, Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        return reactionService.summary(postId, email);
    }

    @PostMapping
    public SummaryRes toggle(@PathVariable Long postId, Authentication auth, @RequestBody ToggleReq req) {
        String email = auth != null ? auth.getName() : null;
        return reactionService.toggle(postId, email, req);
    }
}
