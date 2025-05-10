package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.MediaResponseDto;
import com.pouryat.headless_cms.entity.Media;

public class MediaMapper {

    public static MediaResponseDto toDto(Media media) {
        if (media != null){
            return new MediaResponseDto(
                    media.getId(),
                    media.getType(),
                    media.getUrl()
            );
        }
        return null;
    }
}

