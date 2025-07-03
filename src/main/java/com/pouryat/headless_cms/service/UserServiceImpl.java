package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.auth.jwt.utils.JwtUtils;
import com.pouryat.headless_cms.dto.UpdateUserSubDto;
import com.pouryat.headless_cms.dto.UserResponseDto;
import com.pouryat.headless_cms.dto.UserUpdateDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.handler.CustomException;
import com.pouryat.headless_cms.mapper.UserMapper;
import com.pouryat.headless_cms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto updateUser(User loggedInUser, Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(dto.getPassword());
        user.setRoles(new HashSet<>(List.of(dto.getRole())));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(User loggedInUser, Long id) {
        if (!JwtUtils.isCurrentUserAdmin(loggedInUser)) {
            JwtUtils.checkOwnership(loggedInUser.getId(), id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto getUserById(User loggedInUser, Long id) {
        if (!JwtUtils.isCurrentUserAdmin(loggedInUser)) {
            if (loggedInUser.getId().equals(id)) {
                return UserMapper.toDto(userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found")));
            } else {
                throw new CustomException("cant get other users info!", HttpStatus.FORBIDDEN.value());
            }
        }
        return UserMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public List<UserResponseDto> getAllUsers(int size, int page) {
        Pageable pageable = PageRequest.of(size, page);
        return userRepository.findAll(pageable)
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUserSubscription(Long id, UpdateUserSubDto updateUserSubDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setSubscription(updateUserSubDto.isSubStatus());
        return UserMapper.toDto(userRepository.save(user));
    }
}