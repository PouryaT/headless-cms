package com.pouryat.headless_cms.controller;

import com.pouryat.headless_cms.dto.CommentCreateDto;
import com.pouryat.headless_cms.dto.CommentResponseDto;
import com.pouryat.headless_cms.dto.CommentUpdateDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.resolver.CurrentUser;
import com.pouryat.headless_cms.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentResponseDto create(@CurrentUser User user,
                                     @RequestBody @Valid CommentCreateDto dto) {
        return commentService.createComment(user, dto);
    }

    @GetMapping("/{id}")
    public CommentResponseDto getById(@PathVariable @Positive Long id) {
        return commentService.getCommentById(id);
    }

    @PutMapping("/{id}")
    public CommentResponseDto update(@CurrentUser User user,
                                     @PathVariable @Positive Long id,
                                     @RequestBody @Valid CommentUpdateDto dto) {
        return commentService.updateComment(user, id, dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/update-status")
    public CommentResponseDto updateStatus(@RequestParam(name = "status") Boolean status,
                                           @PathVariable @Positive Long id) {
        return commentService.updateCommentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@CurrentUser User user,
                       @PathVariable @Positive Long id) {
        commentService.deleteComment(user, id);
    }

    @GetMapping("post/{id}")
    public List<CommentResponseDto> getAllByPostId(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return commentService.getCommentByPostId(id, page, size);
    }

    @GetMapping
    public List<CommentResponseDto> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return commentService.getAllComments(page, size);
    }
}