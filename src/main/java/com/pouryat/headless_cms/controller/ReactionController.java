package com.pouryat.headless_cms.controller;

import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.ReactionType;
import com.pouryat.headless_cms.resolver.CurrentUser;
import com.pouryat.headless_cms.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<String> reactToComment(
            @CurrentUser User user,
            @PathVariable Long commentId,
            @RequestParam ReactionType type
    ) {
        reactionService.reactToComment(user, commentId, type);
        return ResponseEntity.ok("Reaction recorded");
    }

    @GetMapping("/comment/{commentId}/stats")
    public ResponseEntity<Map<String, Long>> getReactionStats(@PathVariable Long commentId) {
        return ResponseEntity.ok(reactionService.getReactionStats(commentId));
    }
}
