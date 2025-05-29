package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.CommentCreateDto;
import com.pouryat.headless_cms.dto.CommentResponseDto;
import com.pouryat.headless_cms.dto.CommentUpdateDto;
import com.pouryat.headless_cms.entity.User;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(User user, CommentCreateDto dto);

    CommentResponseDto getCommentById(Long id);

    CommentResponseDto updateCommentStatus(Long id, boolean confirmed);

    CommentResponseDto updateComment(User user, Long id, CommentUpdateDto dto);

    void deleteComment(User user, Long id);

    List<CommentResponseDto> getAllComments(int page, int size);

    List<CommentResponseDto> getCommentByPostId(Long id, int page, int size);
}