package com.pouryat.headless_cms.model;

import com.pouryat.headless_cms.dto.MediaResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinIOUploadResponse {
    private Long id;
    private String url;
    private String name;
    private String type;
    private MediaResponseDto media;
}
