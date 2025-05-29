package com.pouryat.headless_cms.mapper;

import com.pouryat.headless_cms.dto.UserResponseDto;
import com.pouryat.headless_cms.entity.User;

public class UserMapper {

    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}

