package com.blog.afaq.controller;

import com.blog.afaq.dto.request.ChangePasswordRequest;
import com.blog.afaq.dto.request.RequestResetCodeRequest;
import com.blog.afaq.dto.request.ResetPasswordRequest;
import com.blog.afaq.dto.request.VerifyResetCodeRequest;
import com.blog.afaq.dto.response.GenericMessageResponse;
import com.blog.afaq.dto.response.InitiativeResponse;
import com.blog.afaq.dto.response.VerifyResetCodeResponse;
import com.blog.afaq.exception.InvalidTokenException;
import com.blog.afaq.exception.MissingTokenException;
import com.blog.afaq.repository.UserRepository;
import com.blog.afaq.security.JwtTokenProvider;
import com.blog.afaq.service.AuthService;
import com.blog.afaq.service.InitiativeService;
import com.blog.afaq.service.ResetCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final InitiativeService initiativeService;
    private final AuthService authService;
    private final ResetCodeService resetCodeService;

    @GetMapping("/get-all-initiatives")
    public ResponseEntity<List<InitiativeResponse>> getAll() {
        return ResponseEntity.ok(initiativeService.getAllInitiatives());
    }

    @PostMapping("/request-reset")
    public ResponseEntity<GenericMessageResponse> requestReset(@RequestBody RequestResetCodeRequest request) {
        authService.sendResetCode(request.email(), request.channel());
        return ResponseEntity.ok(new GenericMessageResponse("Le code a été envoyé à votre adresse e-mail."));
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<VerifyResetCodeResponse> verifyResetCode(@RequestBody VerifyResetCodeRequest request) {
        boolean valid = resetCodeService.validateCode(request.email(), request.code());
        return ResponseEntity.ok(new VerifyResetCodeResponse(valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password successfully reset."));
    }


}
