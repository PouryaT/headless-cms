package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.PostCreateDto;
import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.entity.*;
import com.pouryat.headless_cms.handler.CustomException;
import com.pouryat.headless_cms.mapper.PostMapper;
import com.pouryat.headless_cms.model.*;
import com.pouryat.headless_cms.repository.CategoryRepository;
import com.pouryat.headless_cms.repository.PostRepository;
import com.pouryat.headless_cms.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final MinIOService minIOService;

    public PostResponseDto createPost(User user, PostCreateDto dto, MultipartFile[] multipartFiles) throws Exception {

        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        List<Tag> tags = tagRepository.findAllById(dto.getTagIds());

        Set<Category> categoriesSet = new HashSet<>(categories);
        Set<Tag> tagsSet = new HashSet<>(tags);
        List<Media> mediaSet = new ArrayList<>();

        Post post = Post.builder().createdAt(LocalDateTime.now()).title(dto.getTitle()).status(PostStatus.PUBLISHED).author(user).tags(tagsSet).categories(categoriesSet).build();

        for (MultipartFile mediaFile : multipartFiles) {
            MinIOUploadResponse minIOResponse = minIOService.uploadFile(mediaFile);
            Media media = new Media();

            media.setUrl(minIOResponse.getUrl());
            media.setPost(post);
            media.setFileName(minIOResponse.getName());
            if (Objects.requireNonNull(mediaFile.getOriginalFilename()).endsWith(".jpg") || Objects.requireNonNull(mediaFile.getOriginalFilename()).endsWith(".png")) {
                media.setType(MediaType.IMAGE);
            }
            mediaSet.add(media);
        }
        post.setMedias(mediaSet);

        Post savedPost = postRepository.save(post);

        return PostMapper.toDto(savedPost);
    }

    public PostWithFileResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        List<String> fileNames = post.getMedias().stream().map(Media::getFileName).toList();
        List<MinIODownloadResponse.FileDownloadMeta> downloadUrls = minIOService.getDownloadUrls(fileNames);

        return new PostWithFileResponseDto(downloadUrls, PostMapper.toDto(post));
    }

    public PostResponseDto updatePost(User user, Long id, PostCreateDto dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(dto.getTitle());
        post.setStatus(PostStatus.EDITED);
        post.setAuthor(user);

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));

        post.setCategories(categories);
        post.setTags(tags);

        return PostMapper.toDto(postRepository.save(post));
    }

    public void deletePost(User user ,Long id) {
        Post post = postRepository.findById(id).orElseThrow(()->new CustomException("Post not found", HttpStatus.NOT_FOUND.value()));

        List<String> fileNames = post.getMedias().stream().map(Media::getFileName).toList();

        postRepository.deleteById(id);
        minIOService.deleteFiles(fileNames);
    }

    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream().map(PostMapper::toDto).collect(Collectors.toList());
    }
}