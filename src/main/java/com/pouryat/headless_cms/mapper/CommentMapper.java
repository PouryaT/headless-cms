package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.CommentCreateDto;
import com.pouryat.headless_cms.dto.CommentResponseDto;
import com.pouryat.headless_cms.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toEntity(CommentCreateDto dto);

    CommentResponseDto toDTO(Comment comment);
}