package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.TagCreateDto;
import com.pouryat.headless_cms.dto.TagResponseDto;
import com.pouryat.headless_cms.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponseDto toDto(Tag tag);

    Tag toEntity(TagCreateDto tagCreateDto);
}