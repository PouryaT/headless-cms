package com.pouryat.headless_cms.controller;

import com.pouryat.headless_cms.dto.PostCreateDto;
import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.PostWithFileResponseDto;
import com.pouryat.headless_cms.resolver.CurrentUser;
import com.pouryat.headless_cms.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PostResponseDto> createPost(@CurrentUser User user,
                                                      @ModelAttribute PostCreateDto postDto,
                                                      @RequestParam("files") MultipartFile[] multipartFiles) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(user, postDto, multipartFiles));
    }

    @GetMapping("/{id}")
    public PostWithFileResponseDto getPost(@PathVariable Long id) throws Exception {
        return postService.getPostById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@CurrentUser User user, @PathVariable Long id, @RequestBody PostCreateDto dto) {
        return ResponseEntity.ok(postService.updatePost(user, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@CurrentUser User user, @PathVariable Long id) {
        postService.deletePost(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }
}