package com.pouryat.headless_cms.auth.controller;

import com.pouryat.headless_cms.auth.model.AuthResponseDto;
import com.pouryat.headless_cms.auth.model.LoginRequestDto;
import com.pouryat.headless_cms.auth.model.RegisterRequestDto;
import com.pouryat.headless_cms.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> logIn(@RequestBody @Valid LoginRequestDto authenticateRequest) {
        return authService.signIn(authenticateRequest);
    }

    @PostMapping("/register")
    public void register(@RequestBody @Valid RegisterRequestDto user) {
        authService.signUp(user);
    }

}
