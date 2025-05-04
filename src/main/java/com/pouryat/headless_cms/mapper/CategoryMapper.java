package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.CategoryCreateDto;
import com.pouryat.headless_cms.dto.CategoryResponseDto;
import com.pouryat.headless_cms.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDto toDto(Category category);

    Category toEntity(CategoryCreateDto categoryCreateDto);
}