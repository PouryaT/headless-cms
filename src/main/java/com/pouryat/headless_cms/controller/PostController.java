package com.pouryat.headless_cms.controller;

import com.pouryat.headless_cms.dto.PostCreateDto;
import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.dto.PostUpdateDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.PostFilter;
import com.pouryat.headless_cms.resolver.CurrentUser;
import com.pouryat.headless_cms.service.PostService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public PostResponseDto createPostWithFiles(@CurrentUser User user,
                                               @ModelAttribute PostCreateDto postDto,
                                               @RequestParam(value = "files", required = false) MultipartFile[] multipartFiles) throws Exception {
        return postService.createPost(user, postDto, multipartFiles);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/post-with-medias", consumes = {"multipart/form-data"})
    public PostResponseDto createPostWithMedias(@CurrentUser User user,
                                                @ModelAttribute PostCreateDto postDto,
                                                @RequestParam Long[] mediaIds) throws Exception {
        return postService.createPost(user, postDto, mediaIds);
    }

    @GetMapping
    public List<PostResponseDto> getAllPosts(@CurrentUser User user,
                                             @ModelAttribute PostFilter filter,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return postService.getAllPostsByFilter(user, filter, page, size);
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@CurrentUser User user,
                                   @PathVariable @Positive Long id) {
        return postService.getPostById(user, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public PostResponseDto updatePost(@CurrentUser User user,
                                      @PathVariable @Positive Long id,
                                      @ModelAttribute PostUpdateDto dto,
                                      @RequestParam(value = "files", required = false) MultipartFile[] multipartFiles) throws Exception {
        return postService.updatePost(user, id, dto, multipartFiles);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/post-with-medias")
    public PostResponseDto updatePostWithMedia(@CurrentUser User user,
                                               @PathVariable @Positive Long id,
                                               @ModelAttribute PostUpdateDto dto,
                                               @RequestParam(value = "mediaIds", required = false) Long[] mediaIds) throws Exception {
        return postService.updatePost(user, id, dto, mediaIds);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deletePost(@CurrentUser User user,
                           @PathVariable @Positive Long id) {
        postService.deletePost(user, id);
    }

    @PostMapping("bookmark/{id}")
    public void bookmark(@CurrentUser User user,
                         @PathVariable @Positive Long id) {
        postService.bookmarkPost(user, id);
    }
}