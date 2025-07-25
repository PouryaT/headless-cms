package com.pouryat.headless_cms.auth.jwt.utils;


import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.exception.CustomException;
import com.pouryat.headless_cms.model.RoleName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private static final String SECRET_KEY = "93734fcb51ad328838a67bdb5ea86fa995a44a110bdfe6fd6c16ab8ddae82fd8839e595ee3715de416bba2d4c7a1aaa48ed1bf2cd57b51f8e4286afe867a30a92588f483ee3b2e16e7c8831637633df723309e5d05ddc68c5a96e452cbcb35abd98b48b31e81550d00f8bd80e8ff1d5175e7b385ccffe649e56319d4f4ff3157b462a32bf8087eb5265e1057a45613045c9963c04ee61ee10618d022e2bc61a2599a9f9173061f7ff8cd5656c67af9d5b5fb7244a631263a7b81b0558affdfd26bf21dbc19ca06ea58b0ffda2332d24538d92cfea2e159b77d4d5320aef52497fc8f04eaa05f11248a751884d60b8403a7e10af2a95b1600aef634f334f37aa2";
    private static final long JWT_EXPIRATION_MS = 86400000; // 24 hours

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails.getUsername());
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static void checkOwnership(Long ownerId, Long currentUserId) {
        if (!ownerId.equals(currentUserId)) {
            throw new CustomException("You are not authorized to perform this action.", HttpStatus.FORBIDDEN.value());
        }
    }

    public static boolean isCurrentUserAdmin(User user) {
        if (user == null || user.getRoles() == null) return true;

        return user.getRoles().stream()
                .noneMatch(role -> role.getRoleName().equals(RoleName.ROLE_ADMIN));
    }
}