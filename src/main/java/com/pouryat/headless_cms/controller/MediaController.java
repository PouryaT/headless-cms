package com.pouryat.headless_cms.controller;

import com.pouryat.headless_cms.dto.MediaResponseDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.model.MinIOUploadResponse;
import com.pouryat.headless_cms.resolver.CurrentUser;
import com.pouryat.headless_cms.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public List<MinIOUploadResponse> uploadMedia(
            @RequestParam("files") MultipartFile[] files,
            @CurrentUser User currentUser
    ) throws Exception {
        return mediaService.uploadMedias(files, currentUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MediaResponseDto> getMedia(@PathVariable Long id) {
        MediaResponseDto media = mediaService.getMediaById(id);
        return ResponseEntity.ok(media);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long[] id) {
        mediaService.deleteMedias(id);
        return ResponseEntity.noContent().build();
    }
}
