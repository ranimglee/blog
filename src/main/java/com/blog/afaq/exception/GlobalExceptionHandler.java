package com.blog.afaq.exception;

import com.blog.afaq.dto.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 409 - Email already exists
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<AuthResponse> handleEmailExists(
            EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new AuthResponse(ex.getMessage(), null));
    }

    // 401 - Wrong email or password
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(ex.getMessage(), null));
    }

    // 423 - Too many attempts
    @ExceptionHandler(UserLockedException.class)
    public ResponseEntity<AuthResponse> handleUserLocked(
            UserLockedException ex) {
        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(new AuthResponse(ex.getMessage(), null));
    }

    // 403 - Email not verified
    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<AuthResponse> handleUserNotActive(
            UserNotActiveException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new AuthResponse(ex.getMessage(), null));
    }

    // 403 - Banned user
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AuthResponse> handleAccessDenied(
            AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new AuthResponse(ex.getMessage(), null));
    }

    // 404 - User not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AuthResponse> handleUserNotFound(
            UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new AuthResponse(ex.getMessage(), null));
    }

    // 401 - Invalid token (refresh, verify, etc.)
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<AuthResponse> handleInvalidToken(
            InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(ex.getMessage(), null));
    }

    // 500 - Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse("Internal server error", null));
    }
}
