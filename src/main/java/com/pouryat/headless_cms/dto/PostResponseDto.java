package com.pouryat.headless_cms.dto;

import com.pouryat.headless_cms.model.PostStatus;
import com.pouryat.headless_cms.model.SubscriptionLevels;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SubscriptionLevels postType;
    private String authorName;
    private Set<String> categories;
    private Set<String> tags;
    private String content;
    private List<MediaResponseDto> medias = new ArrayList<>();
}