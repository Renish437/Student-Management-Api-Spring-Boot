package com.renish.backend.service.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.renish.backend.payload.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** Request attribute used by JwtAuthenticationFilter to explain why the token was rejected. */
    public static final String AUTH_ERROR_ATTRIBUTE = "AUTH_ERROR_MESSAGE";

    public static final String DEFAULT_MESSAGE =
            "Authentication is required. Please login to get a valid token.";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object reason = request.getAttribute(AUTH_ERROR_ATTRIBUTE);
        String message = (reason != null && !reason.toString().isBlank())
                ? reason.toString()
                : DEFAULT_MESSAGE;

        write(response, HttpStatus.UNAUTHORIZED, message, "Unauthorized", request.getRequestURI());
    }

    private void write(HttpServletResponse response,
                       HttpStatus status,
                       Object message,
                       String error,
                       String path) throws IOException {

        ApiResponse<Object> body = ApiResponse.builder()
                .success(false)
                .statusCode(status.value())
                .message(message)
                .data(null)
                .error(error)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
