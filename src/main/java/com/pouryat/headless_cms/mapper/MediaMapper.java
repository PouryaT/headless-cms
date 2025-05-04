package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.MediaCreateDto;
import com.pouryat.headless_cms.dto.MediaResponseDto;
import com.pouryat.headless_cms.entity.Media;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    MediaResponseDto toDto(Media media);

    Media toEntity(MediaCreateDto mediaCreateDto);
}