package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.auth.jwt.utils.JwtUtils;
import com.pouryat.headless_cms.dto.CommentCreateDto;
import com.pouryat.headless_cms.dto.CommentResponseDto;
import com.pouryat.headless_cms.dto.CommentUpdateDto;
import com.pouryat.headless_cms.entity.Comment;
import com.pouryat.headless_cms.entity.Role;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.mapper.CommentMapper;
import com.pouryat.headless_cms.model.RoleName;
import com.pouryat.headless_cms.repository.CommentRepository;
import com.pouryat.headless_cms.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public List<CommentResponseDto> getAllComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findAll(pageable).stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto getCommentById(Long id) {
        return CommentMapper.toDTO(commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found")));
    }

    @Override
    public List<CommentResponseDto> getCommentByPostId(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findAllByPostId(id, pageable).stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto createComment(User user, CommentCreateDto dto) {
        boolean status = false;

        for (Role role : user.getRoles()) {
            if (role.getRoleName().equals(RoleName.ROLE_ADMIN)) {
                status = true;
                break;
            }
        }
        Comment comment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .author(user)
                .content(dto.getText())
                .confirmed(status)
                .post(postRepository.findById(dto.getPostId()).orElseThrow(() -> new RuntimeException("Post not found")))
                .build();

        return CommentMapper.toDTO(commentRepository.save(comment));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public CommentResponseDto updateCommentStatus(Long id, boolean confirmed) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setConfirmed(true);
        return CommentMapper.toDTO(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto updateComment(User user, Long id, CommentUpdateDto dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        JwtUtils.checkOwnership(comment.getAuthor().getId(), user.getId());

        comment.setContent(dto.getText());
        comment.setAuthor(user);
        return CommentMapper.toDTO(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(User user, Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        JwtUtils.checkOwnership(comment.getAuthor().getId(), user.getId());

        commentRepository.deleteById(id);
    }
}
