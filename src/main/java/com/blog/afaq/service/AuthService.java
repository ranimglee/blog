package com.blog.afaq.service;

import com.blog.afaq.dto.request.LoginRequest;
import com.blog.afaq.dto.request.RegisterRequest;
import com.blog.afaq.dto.request.UpdateUserProfileRequest;
import com.blog.afaq.dto.response.LoginResponse;
import com.blog.afaq.dto.response.UserProfileResponse;
import com.blog.afaq.dto.response.UserRegisterResponse;
import com.blog.afaq.exception.*;
import com.blog.afaq.model.*;
import com.blog.afaq.repository.UserRepository;
import com.blog.afaq.repository.VerificationTokenRepository;
import com.blog.afaq.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final ResetCodeService resetCodeService;
    private final TurnstileService turnstileService;


    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 10 * 60 * 1000;

    private final ConcurrentHashMap<String, FailedLoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    // ---------------- REGISTER ----------------

    public UserRegisterResponse register(RegisterRequest request) {

        turnstileService.verify(request.getCaptchaToken());

        String email = normalize(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }

        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCountry(request.getCountry());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCreatedAt(Instant.now());
        user.setStatus(UserStatus.PENDING);

        userRepository.save(user);

        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(token);

        String confirmationLink =
                frontendUrl + "/verify-email?token=" + token.getToken();

        emailService.sendEmailConfirmation(user.getEmail(), confirmationLink);

        return new UserRegisterResponse(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getCountry(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getRole()
        );
    }

    // ---------------- EMAIL VERIFY ----------------

    public boolean verifyEmail(String token) {

        Optional<VerificationToken> vt = verificationTokenRepository.findByToken(token);

        if (vt.isEmpty() || vt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = userRepository.findById(vt.get().getUserId())
                .orElse(null);

        if (user == null) return false;

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        verificationTokenRepository.delete(vt.get());

        return true;
    }

    // ---------------- LOGIN ----------------

    public LoginResponse login(LoginRequest request) {

        turnstileService.verify(request.captchaToken());

        String email = normalize(request.email());

        if (email == null || request.password() == null) {
            throw new InvalidCredentialsException("Missing credentials");
        }

        if (isAccountLocked(email)) {
            throw new UserLockedException("Too many attempts. Try later.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    recordFailedAttempt(email);
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccessDeniedException("Account banned");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserNotActiveException("Account not active");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            recordFailedAttempt(email);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        resetAttempts(email);

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), user.getRole(), user.getId()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, user.getRole());
    }

    // ---------------- ATTEMPTS LOGIC ----------------

    private void recordFailedAttempt(String email) {
        loginAttempts
                .computeIfAbsent(email, k -> new FailedLoginAttempt())
                .increment();
    }

    private void resetAttempts(String email) {
        loginAttempts.remove(email);
    }

    private boolean isAccountLocked(String email) {

        FailedLoginAttempt attempt = loginAttempts.get(email);

        if (attempt == null) return false;

        if (attempt.isExpired()) {
            loginAttempts.remove(email);
            return false;
        }

        return attempt.isLocked();
    }

    private static class FailedLoginAttempt {

        private int attempts;
        private long lockTime;

        void increment() {
            attempts++;
            if (attempts == MAX_ATTEMPTS) {
                lockTime = System.currentTimeMillis();
            }
        }

        boolean isLocked() {
            return attempts >= MAX_ATTEMPTS;
        }

        boolean isExpired() {
            return lockTime > 0 &&
                    System.currentTimeMillis() > lockTime + LOCK_TIME;
        }
    }

    // ---------------- PROFILE ----------------

    public UserProfileResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(normalize(email))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return mapUser(user);
    }

    public UserProfileResponse updateProfile(String email, UpdateUserProfileRequest request) {

        User user = userRepository.findByEmail(normalize(email))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFirstname(request.firstname());
        user.setLastname(request.lastname());
        user.setPhoneNumber(request.phoneNumber());
        user.setCountry(request.country());

        userRepository.save(user);

        return mapUser(user);
    }

    // ---------------- RESET PASSWORD ----------------

    public void sendResetCode(String email) {

        email = normalize(email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            return;
        }

        try {
            String code = resetCodeService.generateCode(email);
            emailService.sendResetPasswordCode(email, code);
        } catch (Exception e) {
            throw new ResetCodeDeliveryException("Failed to send reset code", e);
        }
    }

    public void resetPassword(String email, String otp, String newPassword) {

        email = normalize(email);

        if (!resetCodeService.validateCode(email, otp)) {
            throw new InvalidResetCodeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidResetCodeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetCodeService.deleteCode(email);
    }

    // ---------------- CHANGE PASSWORD ----------------

    public void changePassword(String token, String currentPassword, String newPassword) {

        String email = jwtTokenProvider.extractEmailFromAccessToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ---------------- HELPERS ----------------

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private UserProfileResponse mapUser(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .country(user.getCountry())
                .build();
    }
}