package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.PostResponseDto;
import com.pouryat.headless_cms.entity.Category;
import com.pouryat.headless_cms.entity.Post;
import com.pouryat.headless_cms.entity.Tag;

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
                mapTags(post.getTags())
        );
    }

    static Set<String> mapCategories(Set<Category> categories) {
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
    }

    static Set<String> mapTags(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }
}