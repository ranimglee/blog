package com.blog.afaq.controller;

import com.blog.afaq.dto.request.ChangePasswordRequest;
import com.blog.afaq.dto.request.ResetPasswordRequest;
import com.blog.afaq.dto.request.UpdateUserProfileRequest;
import com.blog.afaq.dto.response.*;
import com.blog.afaq.exception.InvalidTokenException;
import com.blog.afaq.exception.MissingTokenException;
import com.blog.afaq.security.JwtTokenProvider;
import com.blog.afaq.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader(name = "Authorization", required = true) String header,
            @RequestBody ChangePasswordRequest request) {

        String token = extractToken(header);
        authService.changePassword(token, request.currentPassword(), request.newPassword());

        return ResponseEntity.ok(Map.of("message", "Password successfully changed."));
    }

    private String extractToken(String header) {
        if (header == null || header.isBlank()) {
            throw new MissingTokenException();
        }
        if (!header.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid authorization header format.");
        }
        return header.substring(7);
    }
    @PutMapping("/update-my-profile")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @RequestBody UpdateUserProfileRequest request,
            HttpServletRequest httpRequest
    ) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtTokenProvider.extractEmailFromAccessToken(token);

        return ResponseEntity.ok(authService.updateProfile(email, request));
    }

    @GetMapping("/my-profile")
    public ResponseEntity<UserProfileResponse> getClientProfile(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtTokenProvider.extractEmailFromAccessToken(token);

        UserProfileResponse profile = authService.getUserByEmail(email);
        return ResponseEntity.ok(profile);
    }


    @PutMapping("/ban-user/{id}")
    public ResponseEntity<BannedUserDto> banUser(@PathVariable String id) {
        BannedUserDto bannedUser = userService.banUser(id);
        return ResponseEntity.ok(bannedUser);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok("Password has been reset successfully.");
    }




}
