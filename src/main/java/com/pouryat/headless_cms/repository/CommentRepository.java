package com.pouryat.headless_cms.repository;

import com.pouryat.headless_cms.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
