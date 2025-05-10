package com.pouryat.headless_cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Username is required")
    private String username;
    private String email;
    @NotBlank(message = "Username is required")
    @Size(min = 6, max = 10)
    private String password;
}