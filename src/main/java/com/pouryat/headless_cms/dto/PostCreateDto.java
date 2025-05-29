package com.pouryat.headless_cms.dto;

import com.pouryat.headless_cms.model.SubscriptionLevels;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
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
    @NotBlank(message = "Title is required")
    private String title;

    @NotEmpty(message = "At least one category must be selected")
    @Positive
    private Set<Long> categoryIds;

    @NotEmpty(message = "At least one tag must be selected")
    @Positive
    private Set<Long> tagIds;

    @NotEmpty(message = "At least one tag must be selected")
    private SubscriptionLevels postType;
}