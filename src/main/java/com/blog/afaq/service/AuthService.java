package com.blog.afaq.service;



import com.blog.afaq.dto.request.LoginRequest;
import com.blog.afaq.dto.request.RefreshTokenRequest;
import com.blog.afaq.dto.request.RegisterRequest;
import com.blog.afaq.dto.response.AuthResponse;
import com.blog.afaq.dto.response.LoginResponse;
import com.blog.afaq.exception.*;
import com.blog.afaq.model.Role;
import com.blog.afaq.model.User;
import com.blog.afaq.model.UserStatus;
import com.blog.afaq.model.VerificationToken;
import com.blog.afaq.repository.UserRepository;
import com.blog.afaq.repository.VerificationTokenRepository;
import com.blog.afaq.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 10 * 60 * 1000;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    private final ConcurrentHashMap<String, FailedLoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }

        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCountry(request.getCountry());
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(token);

        String confirmationLink = "http://localhost:8080/api/users/verify-email?token=" + token.getToken();
        emailService.sendEmailConfirmation(user.getEmail(), confirmationLink);
        return new AuthResponse("User registered successfully", null);
    }

    public boolean verifyEmail(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken.isEmpty() || verificationToken.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        Optional<User> userOptional = userRepository.findById(verificationToken.get().getUserId());
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken.get());
        return true;
    }



    public LoginResponse login(LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.email());

        if (userOptional.isEmpty()) {
            recordFailedAttempt(request.email());
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        User user = userOptional.get();
        String email = user.getEmail();

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccessDeniedException("Your account is banned.");
        }

        if (isAccountLocked(email)) {
            throw new UserLockedException("Too many failed attempts. Try again later.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            recordFailedAttempt(email);
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new UserNotActiveException("Your account is not active. Please verify your email.");
        }


        loginAttempts.remove(email);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);


        return new LoginResponse(accessToken, refreshToken, user.getRole());
    }

    private void recordFailedAttempt(String email) {
        loginAttempts.put(email, loginAttempts.getOrDefault(email, new FailedLoginAttempt()).increment());
    }

    private boolean isAccountLocked(String email) {
        if (!loginAttempts.containsKey(email)) return false;

        FailedLoginAttempt attempt = loginAttempts.get(email);
        return attempt.isLocked() && attempt.getLockTime() + LOCK_TIME > System.currentTimeMillis();
    }



    private static class FailedLoginAttempt {
        private int attempts;
        private long lockTime;

        public FailedLoginAttempt increment() {
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                lockTime = System.currentTimeMillis();
            }
            return this;
        }

        public boolean isLocked() {
            return attempts >= MAX_ATTEMPTS;
        }

        public long getLockTime() {
            return lockTime;
        }
    }



}
