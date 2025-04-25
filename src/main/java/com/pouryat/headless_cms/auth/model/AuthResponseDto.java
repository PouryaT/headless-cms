package com.pouryat.headless_cms.auth.model;

import com.pouryat.headless_cms.entity.Role;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private String username;
    private String token;
    private long expiration;
    private Set<Role> roles;
}