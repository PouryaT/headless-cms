package com.pouryat.headless_cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateDto implements Serializable {
    @NotBlank(message = "Title is required")
    private String title;

    @NotEmpty(message = "At least one category must be selected")
    private Set<Long> categoryIds;

    @NotEmpty(message = "At least one tag must be selected")
    private Set<Long> tagIds;

    private List<Long> mediaIdsToRemove;
}