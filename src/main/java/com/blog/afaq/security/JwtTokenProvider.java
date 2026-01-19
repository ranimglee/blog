package com.blog.afaq.security;

import com.blog.afaq.model.Role;
import com.blog.afaq.model.User;
import com.blog.afaq.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final UserRepository userRepository;

    public JwtTokenProvider(
            @Value("${app.jwt-secret-access}") String accessSecret,
            @Value("${app.jwt-secret-refresh}") String refreshSecret,
            @Value("${app.jwt-access-expiration-ms}") long accessExpiration,
            @Value("${app.jwt-refresh-expiration-ms}") long refreshExpiration,
            UserRepository userRepository
    ) {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.userRepository = userRepository;
    }

    // Access Token with claims
    public String generateAccessToken(String email, Role role, String userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", "ROLE_" + role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(SignatureAlgorithm.HS256, accessKey)
                .compact();
    }

    // Refresh Token with email only
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(SignatureAlgorithm.HS256, refreshKey)
                .compact();
    }

    public String extractEmailFromAccessToken(String token) {
        return parseClaims(token, accessKey).getSubject();
    }

    public String extractUserIdFromAccessToken(String token) {
        return parseClaims(token, accessKey).get("userId", String.class);
    }

    public String extractRoleFromAccessToken(String token) {
        return parseClaims(token, accessKey).get("role", String.class);
    }

    public String extractEmailFromRefreshToken(String token) {
        return parseClaims(token, refreshKey).getSubject();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    public boolean validateStoredRefreshToken(String token, String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(user -> token.equals(user.getRefreshToken())).orElse(false);
    }

    private boolean validateToken(String token, SecretKey key) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token, SecretKey key) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
