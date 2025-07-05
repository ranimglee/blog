package com.blog.afaq.controller;

import com.blog.afaq.dto.request.ChangePasswordRequest;
import com.blog.afaq.dto.request.UpdateUserProfileRequest;
import com.blog.afaq.dto.response.DownloadLinkResponse;
import com.blog.afaq.dto.response.InitiativeResponse;
import com.blog.afaq.dto.response.UserProfileResponse;
import com.blog.afaq.exception.InvalidTokenException;
import com.blog.afaq.exception.MissingTokenException;
import com.blog.afaq.model.Ressource;
import com.blog.afaq.security.JwtTokenProvider;
import com.blog.afaq.service.AuthService;
import com.blog.afaq.service.CloudinaryService;
import com.blog.afaq.service.InitiativeService;
import com.blog.afaq.service.RessourceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RessourceService ressourceService;
    private final CloudinaryService cloudinaryService;

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









}
