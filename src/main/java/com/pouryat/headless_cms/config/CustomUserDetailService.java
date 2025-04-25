package com.pouryat.headless_cms.config;


import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User currentUser = userRepo.findByUsername(username).orElseThrow(RuntimeException::new);

        return new org.springframework.security.core.userdetails.User(
                currentUser.getUsername(),
                currentUser.getPassword(),
                AuthorityUtils.createAuthorityList(
                        String.valueOf(currentUser.getRoles())
                )
        );
    }
}
