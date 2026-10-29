
package com.renish.backend.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.renish.backend.payload.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ------------------------------------------------------------------
    // Authentication / Authorization
    // ------------------------------------------------------------------

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenExpired(
            TokenExpiredException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Object>> handleIOException(
            IOException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiResponse<Object>> handleFileUpload(
            FileUploadException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenRevoked(
            TokenRevokedException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(
            JwtException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.UNAUTHORIZED,
                "Invalid token or expired token. Please login again.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.UNAUTHORIZED,
                "Invalid email or password. Please try again.");
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Object>> handleDisabled(
            DisabledException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.UNAUTHORIZED,
                "Your account has been disabled. Please contact support.");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleLocked(
            LockedException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.UNAUTHORIZED,
                "Your account has been locked. Please contact support.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest req) {

        String message = (ex.getMessage() == null || ex.getMessage().isBlank())
                ? "Authentication is required. Please login to get a valid token."
                : ex.getMessage();

        return buildError(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.FORBIDDEN,
                "Access denied. You are not authorized to access this resource.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest req) {

        log.warn("ResourceNotFoundException handler called: {}",
                ex.getMessage());

        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(OtpAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleOTPAlreadyVerified(
            OtpAlreadyVerifiedException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOTP(
            InvalidOtpException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(AttemptCountExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleAttemptCountExceeded(
            AttemptCountExceededException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }
    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleOTPExpired(
            OtpExpiredException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(
            ConflictException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<Object>> handleOptimisticLock(
            OptimisticLockException ex,
            HttpServletRequest req) {

        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((fieldError -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }));
        return buildError(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest req) {

        List<String> errors = ex.getAllErrors()
                .stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .distinct()
                .toList();

        return buildError(HttpStatus.BAD_REQUEST, errors);
    }

    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<ApiResponse<Object>> handleValidationException(
    // MethodArgumentNotValidException ex,
    // HttpServletRequest req) {

    // Map<String, List<String>> errors = new HashMap<>();

    // ex.getBindingResult().getFieldErrors().forEach(fieldError ->
    // errors.computeIfAbsent(
    // fieldError.getField(),
    // key -> new ArrayList<>()).add(fieldError.getDefaultMessage()));

    // return buildError(HttpStatus.BAD_REQUEST, errors);
    // }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(
            Exception ex,
            HttpServletRequest req) {

        log.error("Unexpected error on path: {}", req.getRequestURI(), ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
    }

    private ResponseEntity<ApiResponse<Object>> buildError(
            HttpStatus status,
            Object message
            ) {

        ApiResponse<Object> response = ApiResponse.error(
                status.value(),
                message,
                status.getReasonPhrase()

        );

        return ResponseEntity.status(status).body(response);
    }
}
