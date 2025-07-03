package com.blog.afaq.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResetCodeService {

    private static final int EXPIRATION_MINUTES = 10;

    private final Map<String, CodeEntry> resetCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateCode(String email) {
        String code = String.format("%06d", random.nextInt(1000000)); // 6-digit code
        Instant expiresAt = Instant.now().plusSeconds(EXPIRATION_MINUTES * 60);
        resetCodes.put(email, new CodeEntry(code, expiresAt));
        return code;
    }

    public boolean validateCode(String email, String code) {
        CodeEntry entry = resetCodes.get(email);
        if (entry == null) return false;
        if (Instant.now().isAfter(entry.expiresAt())) return false;
        return entry.code().equals(code);
    }

    public void deleteCode(String email) {
        resetCodes.remove(email);
    }

    private record CodeEntry(String code, Instant expiresAt) {}
}
