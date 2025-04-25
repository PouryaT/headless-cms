package com.pouryat.headless_cms.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class LoginRequestDto {
    @NotNull
    @NotBlank(message = "you need to fill username correctly")
    private String username;

    @NotNull
    @NotBlank(message = "you need to fill password correctly,6-8 char")
    @Size(min = 4, max = 8)
    private String password;
}