package com.blog.afaq.controller;

import com.blog.afaq.model.Subscriber;
import com.blog.afaq.repository.SubscriberRepository;
import com.blog.afaq.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;
    private final SubscriberRepository subscriberRepository;


    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        boolean consent = Boolean.TRUE.equals(body.get("consent"));

        try {
            newsletterService.subscribe(email, consent);
            return ResponseEntity.ok("Confirmation email sent!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token) {
        boolean confirmed = newsletterService.confirmSubscription(token);
        if (confirmed) {
            return ResponseEntity.ok("✅ Subscription confirmed!");
        } else {
            return ResponseEntity.badRequest().body("❌ Invalid or expired confirmation link.");
        }
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        Optional<Subscriber> subscriber = subscriberRepository.findByEmail(email);
        if (subscriber.isPresent()) {
            subscriberRepository.delete(subscriber.get());
            return ResponseEntity.ok("You have been unsubscribed.");
        }
        return ResponseEntity.badRequest().body("Email not found.");
    }

}

