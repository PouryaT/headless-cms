package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.MediaResponseDto;
import com.pouryat.headless_cms.entity.Media;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.exception.CustomException;
import com.pouryat.headless_cms.mapper.MediaMapper;
import com.pouryat.headless_cms.model.MinIOUploadResponse;
import com.pouryat.headless_cms.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final StorageService storageService;

    @Override
    public List<MinIOUploadResponse> uploadMedias(MultipartFile[] files, User user) throws Exception {
        return storageService.uploadFile(user, files);
    }

    @Override
    public MediaResponseDto getMediaById(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new CustomException("Media not found with id: " + id, HttpStatus.NOT_FOUND.value()));
        return MediaMapper.toDto(media);
    }

    @Override
    public void deleteMedias(Long[] ids) {
        List<Long> mediaIds = new ArrayList<>(Arrays.asList(ids));
        List<Media> medias = mediaRepository.findAllById(mediaIds);

        List<String> fileNames = medias.stream().map(Media::getFileName).toList();
        storageService.deleteFiles(fileNames);
        mediaRepository.deleteAll(medias);
    }
}