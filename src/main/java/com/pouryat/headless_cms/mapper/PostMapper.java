package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.entity.Category;
import com.pouryat.headless_cms.entity.Post;
import com.pouryat.headless_cms.entity.Tag;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostResponseDto toDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getAuthor().getUsername(),
                mapCategories(post.getCategories()),
                mapTags(post.getTags()),
                post.getContent(),
                Optional.ofNullable(post.getMedias())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(MediaMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    static Set<String> mapCategories(Set<Category> categories) {
        if (categories != null) {
            return categories.stream()
                    .map(Category::getName)
                    .collect(Collectors.toSet());
        }
        return null;
    }

    static Set<String> mapTags(Set<Tag> tags) {
        if (tags != null) {
            return tags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet());
        }
        return null;
    }
}