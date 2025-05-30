package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.PostCreateDto;
import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.dto.PostUpdateDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.PostFilter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(User user, PostCreateDto post, MultipartFile[] multipartFiles) throws Exception;

    PostResponseDto createPost(User user, PostCreateDto dto, Long[] mediaIds) throws Exception;

    PostResponseDto getPostById(User user, Long id);

    PostResponseDto updatePost(User user, Long id, PostUpdateDto dto, MultipartFile[] multipartFiles) throws Exception;

    PostResponseDto updatePost(User user, Long id, PostUpdateDto dto, Long[] mediaIds) throws Exception;

    void deletePost(User user, Long id);

    void bookmarkPost(User user, Long postId);

    List<PostResponseDto> getAllPostsByFilter(User user, PostFilter filter, int page, int size);
}