package com.pouryat.headless_cms.mapper;


import com.pouryat.headless_cms.auth.model.AuthResponseDto;
import com.pouryat.headless_cms.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    AuthResponseDto userToAuthResponseDto(User user);
}