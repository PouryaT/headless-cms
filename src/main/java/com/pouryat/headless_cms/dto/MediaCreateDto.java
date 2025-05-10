package com.pouryat.headless_cms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaCreateDto {
    @NotBlank(message = "media type is required")
    private String mediaType;

    @NotBlank(message = "url is required")
    private String url;
}