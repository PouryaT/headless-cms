package com.pouryat.headless_cms.auth.service;

import com.pouryat.headless_cms.auth.model.AuthResponseDto;
import com.pouryat.headless_cms.auth.model.LoginRequestDto;
import com.pouryat.headless_cms.auth.model.RegisterRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthResponseDto> signIn(LoginRequestDto loginRequestDto);

    void signUp(RegisterRequestDto loginRequestModel);
}
