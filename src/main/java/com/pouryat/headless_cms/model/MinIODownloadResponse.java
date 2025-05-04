package com.pouryat.headless_cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor

public class MinIODownloadResponse {
    private List<FileDownloadMeta> files;

    @Setter
    @Getter
    public static class FileDownloadMeta {
        private String fileName;
        private String url;
    }
}