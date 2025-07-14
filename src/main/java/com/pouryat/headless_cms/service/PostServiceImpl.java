package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.auth.jwt.utils.JwtUtils;
import com.pouryat.headless_cms.dto.PostCreateDto;
import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.dto.PostUpdateDto;
import com.pouryat.headless_cms.entity.*;
import com.pouryat.headless_cms.exception.CustomException;
import com.pouryat.headless_cms.mapper.PostMapper;
import com.pouryat.headless_cms.model.MinIOUploadResponse;
import com.pouryat.headless_cms.model.PostFilter;
import com.pouryat.headless_cms.model.PostStatus;
import com.pouryat.headless_cms.model.SubscriptionLevels;
import com.pouryat.headless_cms.repository.CategoryRepository;
import com.pouryat.headless_cms.repository.MediaRepository;
import com.pouryat.headless_cms.repository.PostRepository;
import com.pouryat.headless_cms.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final StorageService storageService;
    private final EntityManager entityManager;
    private final MediaRepository mediaRepository;

    @Transactional
    @Override
    public PostResponseDto createPost(User user, PostCreateDto dto, MultipartFile[] multipartFiles) throws Exception {
        Set<Category> categoriesSet = null;
        Set<Tag> tagsSet = null;

        if (dto.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            categoriesSet = new HashSet<>(categories);
        }

        if (dto.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
            tagsSet = new HashSet<>(tags);
        }

        Post post = Post.builder().createdAt(LocalDateTime.now()).title(dto.getTitle()).status(PostStatus.PUBLISHED).author(user).tags(tagsSet).categories(categoriesSet).build();

        if (multipartFiles != null) {
            List<MinIOUploadResponse> minIOUploadResponseList = storageService.uploadFile(user, multipartFiles);
            post.setMedias(minIOUploadResponseList.stream().map(x -> Media.builder()
                    .url(x.getUrl())
                    .post(post)
                    .type(x.getType())
                    .fileName(x.getName())
                    .id(x.getId())
                    .build()).toList()
            );
            return PostMapper.toDto(postRepository.save(post));
        }
        return PostMapper.toDto(postRepository.save(post));
    }

    @Transactional
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
    @Cacheable(value = "posts")
    public List<PostResponseDto> getAllPostsByFilter(User user, PostFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> cq = cb.createQuery(Post.class);
        Root<Post> post = cq.from(Post.class);
        cq.select(post).distinct(true);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getAuthorId() != null) {
            predicates.add(cb.equal(post.get("author").get("id"), filter.getAuthorId()));
        }

        if (!user.isSubscription()) {
            predicates.add(cb.equal(post.get("postType"), SubscriptionLevels.PUBLIC_POST));
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

        return posts.stream().map(PostMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "posts")
    public PostResponseDto getPostById(User user, Long id) {
        if (user.isSubscription()) {
            Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
            return PostMapper.toDto(post);
        }
        Post post = postRepository.findById(id).filter(p -> p.getPostType().equals(SubscriptionLevels.PUBLIC_POST)).orElseThrow(() -> new RuntimeException("Post not found"));
        return PostMapper.toDto(post);
    }

    @Transactional
    @Override
    public PostResponseDto updatePost(User user, Long id, PostUpdateDto dto, MultipartFile[] multipartFiles) throws Exception {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        JwtUtils.checkOwnership(post.getAuthor().getId(), user.getId());

        if (post.getMedias() != null && dto.getMediaIdsToRemove() != null) {
            List<Media> mediasToRemove = post.getMedias().stream()
                    .filter(m -> dto.getMediaIdsToRemove().contains(m.getId()))
                    .toList();
            mediasToRemove.forEach(media -> {
                media.setPost(null);
                post.getMedias().remove(media);
            });
            storageService.deleteFiles(mediasToRemove.stream().map(Media::getFileName).toList());
        }


        if (dto.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            Set<Category> categoriesSet = new HashSet<>(categories);
            post.setCategories(categoriesSet);
        }

        if (dto.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
            Set<Tag> tagsSet = new HashSet<>(tags);
            post.setTags(tagsSet);
        }

        post.setAuthor(user);
        post.setUpdatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.EDITED);

        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
        }

        if (dto.getPostType() != null) {
            post.setPostType(dto.getPostType());
        }

        if (multipartFiles != null) {
            List<MinIOUploadResponse> minIOUploadResponseList = storageService.uploadFile(user, multipartFiles);
            post.getMedias().addAll(minIOUploadResponseList.stream().map(x -> Media.builder()
                            .url(x.getUrl())
                            .post(post)
                            .type(x.getType())
                            .fileName(x.getName())
                            .id(x.getId())
                            .build())
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        return PostMapper.toDto(postRepository.save(post));
    }

    @Transactional
    @Override
    public PostResponseDto updatePost(User user, Long id, PostUpdateDto dto, Long[] mediaIds) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        JwtUtils.checkOwnership(post.getAuthor().getId(), user.getId());

        if (post.getMedias() != null && dto.getMediaIdsToRemove() != null) {
            List<Media> mediasToRemove = post.getMedias().stream()
                    .filter(m -> dto.getMediaIdsToRemove().contains(m.getId()))
                    .toList();
            mediasToRemove.forEach(media -> {
                media.setPost(null);
                post.getMedias().remove(media);
            });
            storageService.deleteFiles(mediasToRemove.stream().map(Media::getFileName).toList());
        }

        if (mediaIds != null) {
            List<Long> mediaIdsList = new ArrayList<>(Arrays.asList(mediaIds));
            List<Media> medias = mediaRepository.findAllById(mediaIdsList);
            post.getMedias().addAll(medias);
        }


        if (dto.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            Set<Category> categoriesSet = new HashSet<>(categories);
            post.setCategories(categoriesSet);
        }

        if (dto.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
            Set<Tag> tagsSet = new HashSet<>(tags);
            post.setTags(tagsSet);
        }

        post.setAuthor(user);
        post.setUpdatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.EDITED);

        assert dto.getTitle() != null;
        post.setTitle(dto.getTitle());

        return PostMapper.toDto(postRepository.save(post));
    }

    @Transactional
    @Override
    @CacheEvict(value = "posts")
    public void deletePost(User user, Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND.value()));
        JwtUtils.checkOwnership(post.getAuthor().getId(), user.getId());

        List<String> fileNames = post.getMedias().stream().map(Media::getFileName).toList();

        postRepository.deleteById(id);
        storageService.deleteFiles(fileNames);
    }

    @Override
    public void bookmarkPost(User user, Long postId) {
        assert postId != null;
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException("Post not found", 404)
        );
        Set<Post> bookmarkPost = new HashSet<>();
        bookmarkPost.add(post);
        user.setBookmarkedPosts(bookmarkPost);
    }
}