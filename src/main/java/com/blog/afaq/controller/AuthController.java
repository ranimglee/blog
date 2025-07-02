package com.blog.afaq.controller;

import com.blog.afaq.dto.request.LoginRequest;
import com.blog.afaq.dto.request.RefreshTokenRequest;
import com.blog.afaq.dto.response.AuthResponse;
import com.blog.afaq.dto.response.LoginResponse;
import com.blog.afaq.dto.request.RegisterRequest;
import com.blog.afaq.exception.InvalidTokenException;
import com.blog.afaq.exception.UserNotFoundException;
import com.blog.afaq.model.User;
import com.blog.afaq.repository.UserRepository;
import com.blog.afaq.security.JwtTokenProvider;
import com.blog.afaq.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token.");
        }

        String email = jwtTokenProvider.extractEmailFromRefreshToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!jwtTokenProvider.validateStoredRefreshToken(refreshToken, email)) {
            throw new InvalidTokenException("Invalid refresh token.");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        return ResponseEntity.ok(new LoginResponse(newAccessToken, refreshToken, user.getRole()));
    }
}
