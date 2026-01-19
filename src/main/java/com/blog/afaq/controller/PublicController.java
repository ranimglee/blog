package com.blog.afaq.controller;

import com.blog.afaq.dto.request.ContactMessageRequest;
import com.blog.afaq.dto.request.RequestResetCodeRequest;
import com.blog.afaq.dto.request.ResetPasswordRequest;
import com.blog.afaq.dto.request.VerifyResetCodeRequest;
import com.blog.afaq.dto.response.*;

import com.blog.afaq.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final InitiativeService initiativeService;
    private final AuthService authService;
    private final ResetCodeService resetCodeService;
    private final ArticleService articleService;
    private final RessourceService ressourceService;
    private final ContactService contactService;

    @GetMapping("/get-all-initiatives")
    public ResponseEntity<List<InitiativeResponse>> getAllInitiatives() {
        return ResponseEntity.ok(initiativeService.getAllInitiatives());
    }

    @PostMapping("/request-reset")
    public ResponseEntity<GenericMessageResponse> requestReset(@RequestBody RequestResetCodeRequest request) {
        authService.sendResetCode(request.email());
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

    @GetMapping("/get-all-article")
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/get-article-by/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable String id) {
        return articleService.getArticleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/get-all-resources")
    public ResponseEntity<List<RessourceResponse>> getAllResources() {
        return ResponseEntity.ok(ressourceService.getAll());
    }

    @GetMapping("/get-resource-by/{id}")
    public ResponseEntity<RessourceResponse> getResourceById(@PathVariable String id) {
        return ResponseEntity.ok(ressourceService.getById(id));
    }


    @PostMapping("/send-message")
    public ResponseEntity<String> sendContactMessage(@Valid @RequestBody ContactMessageRequest request) {
        contactService.processMessage(request);
        return ResponseEntity.ok("Message sent successfully!");
    }

    @GetMapping("/get-initiative-by/{id}")
    public ResponseEntity<InitiativeResponse> getById(@PathVariable String id) {
        return initiativeService.getInitiativeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
