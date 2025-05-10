package com.pouryat.headless_cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostFilter {
    private LocalDateTime createdAt;
    private Long authorId;
    private List<Long> categoryIds = new ArrayList<>();
    private List<Long> tagIds = new ArrayList<>();
}