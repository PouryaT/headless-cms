package com.pouryat.headless_cms.model;

import com.pouryat.headless_cms.dto.PostResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostWithFileResponseDto {
    private List<MinIODownloadResponse.FileDownloadMeta> fileDownloadMetas;
    private PostResponseDto postResponseDto;
}
