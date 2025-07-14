package com.pouryat.headless_cms.repository;

import com.pouryat.headless_cms.entity.Comment;
import com.pouryat.headless_cms.exception.CustomException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long post_id, Pageable pageable) throws CustomException;
}
