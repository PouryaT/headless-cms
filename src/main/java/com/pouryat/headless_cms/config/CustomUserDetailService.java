package com.pouryat.headless_cms.config;

import com.pouryat.headless_cms.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("UserDetailsService: loading user by username: " + username);
        return userRepo.findByUsername(username)
                .map(UserPrincipal::fromUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}