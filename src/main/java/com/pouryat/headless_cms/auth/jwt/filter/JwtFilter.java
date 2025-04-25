package com.pouryat.headless_cms.auth.jwt.filter;


import com.pouryat.headless_cms.auth.jwt.utils.JwtUtils;
import com.pouryat.headless_cms.config.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    private final CustomUserDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException, IOException {
        String token = extractToken(request);
        if (token != null && jwtUtils.isTokenValid(token, userDetailsService.loadUserByUsername(jwtUtils.extractUsername(token)))) {
            String email = jwtUtils.extractUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(email);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        }
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }
}
