package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.CommentResponseDto;
import com.pouryat.headless_cms.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public static CommentResponseDto toDTO(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getUsername(),
                comment.getPost().getId(),
                comment.getCreatedAt(),
                comment.getConfirmed()
        );
    }
}