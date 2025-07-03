package com.blog.afaq.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResetCodeService {

    private static final int EXPIRATION_MINUTES = 10;
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateCode(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000)); // 6-digit code
        Instant expiresAt = Instant.now().plusSeconds(EXPIRATION_MINUTES * 60);
        codeStore.put(email, new CodeEntry(code, expiresAt));
        return code;
    }

    public boolean validateCode(String email, String code) {
        CodeEntry entry = codeStore.get(email);
        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            return false;
        }
        return entry.code().equals(code);
    }

    public void deleteCode(String email) {
        codeStore.remove(email);
    }

    private record CodeEntry(String code, Instant expiresAt) {}
}
