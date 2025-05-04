package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.CommentCreateDto;
import com.pouryat.headless_cms.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentCreateDto dto);

    CommentResponseDto getCommentById(Long id);

    CommentResponseDto updateComment(Long id, CommentCreateDto dto);

    void deleteComment(Long id);

    List<CommentResponseDto> getAllComments();
}