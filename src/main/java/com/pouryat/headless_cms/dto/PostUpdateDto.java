package com.pouryat.headless_cms.dto;

import com.pouryat.headless_cms.model.SubscriptionLevels;
import jakarta.validation.constraints.Positive;
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
    private String title;
    @Positive
    private Set<Long> categoryIds;
    @Positive
    private Set<Long> tagIds;
    @Positive
    private List<Long> mediaIdsToRemove;
    private SubscriptionLevels postType;
}