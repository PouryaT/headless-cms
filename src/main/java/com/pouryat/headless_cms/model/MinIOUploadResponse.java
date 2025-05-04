package com.pouryat.headless_cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinIOUploadResponse {
    private String url;
    private String name;
}
