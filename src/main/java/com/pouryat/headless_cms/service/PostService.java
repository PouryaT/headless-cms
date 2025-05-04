package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.PostCreateDto;
import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.PostWithFileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(User user, PostCreateDto post, MultipartFile[] multipartFiles) throws Exception;

    List<PostResponseDto> getAllPosts();

    PostWithFileResponseDto getPostById(Long id) throws Exception;

    PostResponseDto updatePost(User user, Long id, PostCreateDto post);

    void deletePost(User user, Long id);
}