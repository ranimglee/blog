package com.blog.afaq.service;

import com.blog.afaq.dto.request.LoginRequest;
import com.blog.afaq.dto.request.RegisterRequest;
import com.blog.afaq.dto.request.UpdateUserProfileRequest;
import com.blog.afaq.dto.response.LoginResponse;
import com.blog.afaq.dto.response.UserProfileResponse;
import com.blog.afaq.dto.response.UserRegisterResponse;
import com.blog.afaq.exception.*;
import com.blog.afaq.model.*;
import com.blog.afaq.repository.AccessLogRepository;
import com.blog.afaq.repository.UserRepository;
import com.blog.afaq.repository.VerificationTokenRepository;
import com.blog.afaq.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 10 * 60 * 1000;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final ResetCodeService resetCodeService;
    private final AccessLogRepository accessLogRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final ConcurrentHashMap<String, FailedLoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public UserRegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + request.getEmail() + " already exists");
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
        user.setCreatedAt(Instant.now());
        user.setStatus(UserStatus.PENDING);


        userRepository.save(user);
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(token);

        String confirmationLink = "https://afaqgulfcoop.com/api/auth/verify-email?token=" + token.getToken();

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
        );    }

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

        accessLogRepository.save(new AccessLog(null, user.getEmail(), Instant.now()));

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

    public UserProfileResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        return UserProfileResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .country(user.getCountry())
                .build();
    }

    public UserProfileResponse updateProfile(String email, UpdateUserProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFirstname(request.firstname());
        user.setLastname(request.lastname());
        user.setPhoneNumber(request.phoneNumber());
        user.setCountry(request.country());


        userRepository.save(user);

        return UserProfileResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .country(user.getCountry())
                .build();
    }



    public void sendResetCode(String email) {
        log.info("📩 Attempting to send reset code to {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("⚠️ No user found with email: {}", email);
            return;
        }

        User user = userOpt.get();

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            log.warn("⛔ User {} is not active. Cannot send reset code.", email);
            return;
        }

        try {
            String code = resetCodeService.generateCode(email);
            log.info("🔑 Generated reset code for user {}: {}", email, code);

            emailService.sendResetPasswordCode(user.getEmail(), code);
            log.info("✅ Reset code successfully sent to {}", email);

        } catch (Exception e) {
            log.error("❌ Failed to send reset code for user {}: {}", email, e.getMessage());
            throw new ResetCodeDeliveryException("Failed to send reset code for user: " + email, e);
        }
    }

    public void resetPassword(String email, String otpCode, String newPassword) {
        log.info("🔄 Attempting to reset password for user {}", email);

        if (!resetCodeService.validateCode(email, otpCode)) {
            log.warn("⚠️ Invalid or expired OTP code for user {}", email);
            throw new InvalidResetCodeException("Invalid or expired OTP.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("⚠️ No user found with email: {}", email);
                    return new InvalidResetCodeException("Invalid user.");
                });

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("✅ Password successfully reset for user {}", email);

        resetCodeService.deleteCode(email);
        log.info("🗑️ OTP code deleted for user {}", email);
    }


    public void changePassword(String token, String currentPassword, String newPassword) {
        String email = jwtTokenProvider.extractEmailFromAccessToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found."));

        // Check if the current password matches the stored password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }




}
