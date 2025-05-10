package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.auth.jwt.utils.JwtUtils;
import com.pouryat.headless_cms.dto.PostCreateDto;
import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.dto.PostUpdateDto;
import com.pouryat.headless_cms.entity.*;
import com.pouryat.headless_cms.handler.CustomException;
import com.pouryat.headless_cms.mapper.PostMapper;
import com.pouryat.headless_cms.model.PostFilter;
import com.pouryat.headless_cms.model.PostStatus;
import com.pouryat.headless_cms.repository.CategoryRepository;
import com.pouryat.headless_cms.repository.MediaRepository;
import com.pouryat.headless_cms.repository.PostRepository;
import com.pouryat.headless_cms.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final StorageService storageService;
    private final EntityManager entityManager;
    private final MediaRepository mediaRepository;

    @Override
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        return PostMapper.toDto(post);
    }

    @Override
    public PostResponseDto createPost(User user, PostCreateDto dto, MultipartFile[] multipartFiles) throws Exception {
        Set<Category> categoriesSet = null;
        Set<Tag> tagsSet = null;

        if (dto.getCategoryIds() != null) {
            List<Category> categories = categories = categoryRepository.findAllById(dto.getCategoryIds());
            categoriesSet = new HashSet<>(categories);
        }

        if (dto.getTagIds() != null) {
            List<Tag> tags = tags = tagRepository.findAllById(dto.getTagIds());
            tagsSet = new HashSet<>(tags);
        }

        Post post = Post.builder().createdAt(LocalDateTime.now()).title(dto.getTitle()).status(PostStatus.PUBLISHED).author(user).tags(tagsSet).categories(categoriesSet).build();

        if (multipartFiles != null) {
            storageService.uploadFile(user, multipartFiles, post);
            return PostMapper.toDto(postRepository.save(post));
        }
        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostResponseDto createPost(User user, PostCreateDto dto, Long[] mediaIds) {

        List<Media> medias = null;
        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        List<Tag> tags = tagRepository.findAllById(dto.getTagIds());

        Set<Category> categoriesSet = new HashSet<>(categories);
        Set<Tag> tagsSet = new HashSet<>(tags);

        if (mediaIds != null) {
            List<Long> mediaIdsList = new ArrayList<>(Arrays.asList(mediaIds));
            medias = mediaRepository.findAllById(mediaIdsList);
        }
        Post post = Post.builder().createdAt(LocalDateTime.now()).title(dto.getTitle()).status(PostStatus.PUBLISHED).author(user).tags(tagsSet).categories(categoriesSet).medias(medias).build();

        Post savedPost = postRepository.save(post);

        return PostMapper.toDto(savedPost);
    }

    @Override
    public PostResponseDto updatePost(User user, Long id, PostUpdateDto dto, MultipartFile[] multipartFiles) throws Exception {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        JwtUtils.checkOwnership(post.getAuthor().getId(), user.getId());

        List<String> fileNamesToRemove = post.getMedias().stream().filter(m -> dto.getMediaIdsToRemove().contains(m.getId())).map(Media::getFileName).toList();

        storageService.deleteFiles(fileNamesToRemove);

        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        List<Tag> tags = tagRepository.findAllById(dto.getTagIds());

        Set<Category> categoriesSet = new HashSet<>(categories);
        Set<Tag> tagsSet = new HashSet<>(tags);

        post.setAuthor(user);
        post.setUpdatedAt(LocalDateTime.now());
        post.setTags(tagsSet);
        post.setStatus(PostStatus.EDITED);
        post.setTitle(dto.getTitle());
        post.setCategories(categoriesSet);
        storageService.uploadFile(user, multipartFiles, post);

        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostResponseDto updatePost(User user, Long id, PostUpdateDto dto, Long[] mediaIds) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        JwtUtils.checkOwnership(post.getAuthor().getId(), user.getId());

        List<String> fileNamesToRemove = post.getMedias().stream().filter(m -> dto.getMediaIdsToRemove().contains(m.getId())).map(Media::getFileName).toList();
        List<Long> mediaIdsList = new ArrayList<>(Arrays.asList(mediaIds));
        List<Media> medias = mediaRepository.findAllById(mediaIdsList);

        storageService.deleteFiles(fileNamesToRemove);

        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        List<Tag> tags = tagRepository.findAllById(dto.getTagIds());

        Set<Category> categoriesSet = new HashSet<>(categories);
        Set<Tag> tagsSet = new HashSet<>(tags);

        post.setAuthor(user);
        post.setUpdatedAt(LocalDateTime.now());
        post.setTags(tagsSet);
        post.setStatus(PostStatus.EDITED);
        post.setTitle(dto.getTitle());
        post.setCategories(categoriesSet);
        post.setMedias(medias);

        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public void deletePost(User user, Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND.value()));
        JwtUtils.checkOwnership(post.getAuthor().getId(), user.getId());

        List<String> fileNames = post.getMedias().stream().map(Media::getFileName).toList();

        postRepository.deleteById(id);
        storageService.deleteFiles(fileNames);
    }

    @Override
    public List<PostResponseDto> filterPosts(PostFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> cq = cb.createQuery(Post.class);
        Root<Post> post = cq.from(Post.class);
        cq.select(post).distinct(true);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getAuthorId() != null) {
            predicates.add(cb.equal(post.get("author").get("id"), filter.getAuthorId()));
        }

        if (filter.getCreatedAt() != null) {
            predicates.add(cb.greaterThanOrEqualTo(post.get("createdAt"), filter.getCreatedAt()));
        }

        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            Join<Object, Object> categories = post.join("categories", JoinType.LEFT);
            predicates.add(categories.get("id").in(filter.getCategoryIds()));
        }

        if (filter.getTagIds() != null && !filter.getTagIds().isEmpty()) {
            Join<Object, Object> tags = post.join("tags", JoinType.LEFT);
            predicates.add(tags.get("id").in(filter.getTagIds()));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Post> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Post> posts = query.getResultList();

        // 👇 Convert to DTOs
        return posts.stream().map(PostMapper::toDto).collect(Collectors.toList());

    }
}