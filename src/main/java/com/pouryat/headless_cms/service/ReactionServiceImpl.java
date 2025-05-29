package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.entity.Comment;
import com.pouryat.headless_cms.entity.Reaction;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.ReactionType;
import com.pouryat.headless_cms.repository.CommentRepository;
import com.pouryat.headless_cms.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;

    public void reactToComment(User user, Long commentId, ReactionType type) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<Reaction> existing = reactionRepository.findByUserIdAndCommentId(user.getId(), commentId);

        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            if (reaction.getType() == type) {
                reactionRepository.delete(reaction);
            } else {
                reaction.setType(type);
                reactionRepository.save(reaction);
            }
        } else {
            Reaction reaction = new Reaction();
            reaction.setComment(comment);
            reaction.setUser(user);
            reaction.setType(type);
            reactionRepository.save(reaction);
        }
    }

    public Map<String, Long> getReactionStats(Long commentId) {
        long likes = reactionRepository.countByCommentIdAndType(commentId, ReactionType.LIKE);
        long dislikes = reactionRepository.countByCommentIdAndType(commentId, ReactionType.DISLIKE);

        Map<String, Long> result = new HashMap<>();
        result.put("likes", likes);
        result.put("dislikes", dislikes);
        return result;
    }
}