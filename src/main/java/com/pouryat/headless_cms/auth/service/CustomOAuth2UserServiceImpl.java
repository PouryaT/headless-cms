package com.pouryat.headless_cms.auth.service;

import com.pouryat.headless_cms.config.UserPrincipal;
import com.pouryat.headless_cms.entity.Role;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.exception.CustomException;
import com.pouryat.headless_cms.repository.RoleRepository;
import com.pouryat.headless_cms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOAuth2UserServiceImpl implements CustomOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public CustomOAuth2UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(1L).orElseThrow(() -> new CustomException("role not found", 404)));

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(oAuth2User.getAttribute("email"));
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    newUser.setRoles(roles);
                    return userRepository.save(newUser);
                });

        return UserPrincipal.fromUser(user, oAuth2User.getAttributes());
    }
}
