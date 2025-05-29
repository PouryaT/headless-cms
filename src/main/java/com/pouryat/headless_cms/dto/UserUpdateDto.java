package com.pouryat.headless_cms.dto;

import com.pouryat.headless_cms.entity.Role;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String username;

    @Size(min = 6, max = 10)
    private String password;

    private Role[] role;
}