package com.pouryat.headless_cms.repository;

import com.pouryat.headless_cms.entity.Reaction;
import com.pouryat.headless_cms.model.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserIdAndCommentId(Long userId, Long commentId);

    long countByCommentIdAndType(Long commentId, ReactionType type);
}
