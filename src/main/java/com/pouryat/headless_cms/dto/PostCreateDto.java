package com.pouryat.headless_cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto implements Serializable {
    private String title;
    private Set<Long> categoryIds;
    private Set<Long> tagIds;
}