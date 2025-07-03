package com.blog.afaq.controller;

import com.blog.afaq.dto.request.ChangePasswordRequest;
import com.blog.afaq.dto.response.InitiativeResponse;
import com.blog.afaq.exception.InvalidTokenException;
import com.blog.afaq.exception.MissingTokenException;
import com.blog.afaq.service.AuthService;
import com.blog.afaq.service.InitiativeService;
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

}
