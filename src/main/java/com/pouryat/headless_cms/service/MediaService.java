package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.MediaResponseDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.MinIOUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    List<MinIOUploadResponse> uploadMedias(MultipartFile[] files, User user, Long postId) throws Exception;

    MediaResponseDto getMediaById(Long id);

    void deleteMedias(Long[] ids);
}
