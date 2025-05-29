package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.ReactionType;

import java.util.Map;

public interface ReactionService {
    void reactToComment(User user, Long commentId, ReactionType type);

    Map<String, Long> getReactionStats(Long commentId);
}