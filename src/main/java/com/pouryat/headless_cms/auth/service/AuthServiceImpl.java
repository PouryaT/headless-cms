package com.pouryat.headless_cms.auth.service;


import com.pouryat.headless_cms.auth.jwt.utils.JwtUtils;
import com.pouryat.headless_cms.auth.model.AuthResponseDto;
import com.pouryat.headless_cms.auth.model.LoginRequestDto;
import com.pouryat.headless_cms.auth.model.RegisterRequestDto;
import com.pouryat.headless_cms.config.CustomUserDetailService;
import com.pouryat.headless_cms.entity.Role;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.handler.CustomException;
import com.pouryat.headless_cms.mapper.AuthMapper;
import com.pouryat.headless_cms.repository.RoleRepository;
import com.pouryat.headless_cms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final CustomUserDetailService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final AuthMapper authMapper;

    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity<AuthResponseDto> signIn(LoginRequestDto authenticateRequest) throws CustomException {
        User user = userRepository.findByUsername(authenticateRequest.getUsername()).orElseThrow(RuntimeException::new);

        if (user.getUsername().equals(authenticateRequest.getUsername()) && passwordEncoder.matches(authenticateRequest.getPassword(), user.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticateRequest.getUsername(), authenticateRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtUtils.generateToken(userDetailsService.loadUserByUsername(authenticateRequest.getUsername()));
                return ResponseEntity.ok(new AuthResponseDto(authenticateRequest.getUsername(), token, jwtUtils.extractExpiration(token).getTime(), user.getRoles()));
            } else {
                throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED.value());
            }
        } else {
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    public ResponseEntity<AuthResponseDto> signUp(RegisterRequestDto registerRequestDto) throws CustomException {
        if (userRepository.findByUsername(registerRequestDto.getUsername()).isPresent()) {
            throw new CustomException("Username already exists", HttpStatus.CONFLICT.value());
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(1L).orElseThrow(() ->new CustomException("role not found",404)));

        User newUser = User.builder().username(registerRequestDto.getUsername()).password(passwordEncoder.encode(registerRequestDto.getPassword())).roles(roles).build();
        return new ResponseEntity<>(authMapper.userToAuthResponseDto(userRepository.save(newUser)), HttpStatus.OK);
    }
}