package com.pouryat.headless_cms.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}