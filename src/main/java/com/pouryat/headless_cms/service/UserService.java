package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.dto.UpdateUserSubDto;
import com.pouryat.headless_cms.dto.UserResponseDto;
import com.pouryat.headless_cms.dto.UserUpdateDto;
import com.pouryat.headless_cms.entity.User;

import java.util.List;

public interface UserService {
    UserResponseDto updateUser(User user, Long id, UserUpdateDto dto);

    void deleteUser(User user, Long id);

    UserResponseDto getUserById(User user, Long id);

    List<UserResponseDto> getAllUsers(int size, int page);

    UserResponseDto updateUserSubscription(Long id, UpdateUserSubDto updateUserSubDto);
}