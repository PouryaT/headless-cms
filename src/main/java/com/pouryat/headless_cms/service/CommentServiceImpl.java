package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.CommentCreateDto;
import com.pouryat.headless_cms.dto.CommentResponseDto;
import com.pouryat.headless_cms.entity.Comment;
import com.pouryat.headless_cms.mapper.CommentMapper;
import com.pouryat.headless_cms.repository.CommentRepository;
import com.pouryat.headless_cms.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto createComment(CommentCreateDto dto) {
        Comment comment = commentMapper.toEntity(dto);
        comment.setPost(postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found")));
        comment.setAuthorName(SecurityContextHolder.getContext().getAuthentication().getName());
        return commentMapper.toDTO(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto getCommentById(Long id) {
        return commentMapper.toDTO(commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found")));
    }

    @Override
    public CommentResponseDto updateComment(Long id, CommentCreateDto dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(dto.getText());
        return commentMapper.toDTO(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<CommentResponseDto> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
