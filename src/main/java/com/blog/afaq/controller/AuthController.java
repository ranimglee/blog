package com.blog.afaq.controller;

import com.blog.afaq.dto.request.*;
import com.blog.afaq.dto.response.*;
import com.blog.afaq.exception.InvalidTokenException;
import com.blog.afaq.exception.UserNotFoundException;
import com.blog.afaq.model.User;
import com.blog.afaq.repository.UserRepository;
import com.blog.afaq.security.JwtTokenProvider;
import com.blog.afaq.service.AuthService;
import com.blog.afaq.service.ResetCodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody RegisterRequest request) {
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

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = authService.verifyEmail(token);
        if (verified) {
            return ResponseEntity.ok("Email successfully verified! You can now log in.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }



}
