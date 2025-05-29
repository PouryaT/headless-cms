package com.pouryat.headless_cms.controller;

import com.pouryat.headless_cms.dto.UpdateUserSubDto;
import com.pouryat.headless_cms.dto.UserResponseDto;
import com.pouryat.headless_cms.dto.UserUpdateDto;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.resolver.CurrentUser;
import com.pouryat.headless_cms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@CurrentUser User user,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(user, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUser User user,
                                       @PathVariable Long id) {
        userService.deleteUser(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@CurrentUser User user,
                                                   @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(user, id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(size, page));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("update-subscription/{id}")
    public ResponseEntity<UserResponseDto> updateSub(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateUserSubDto updateUserSubDto) {
        return ResponseEntity.ok(userService.updateUserSubscription(id, updateUserSubDto));
    }
}