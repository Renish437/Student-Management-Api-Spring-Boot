package com.renish.backend.service.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.renish.backend.payload.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    public static final String DEFAULT_MESSAGE =
            "Access denied. You are not authorized to access this resource.";

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        Object reason = request.getAttribute(JwtAuthenticationEntryPoint.AUTH_ERROR_ATTRIBUTE);
        String message = (reason != null && !reason.toString().isBlank())
                ? reason.toString()
                : DEFAULT_MESSAGE;

        ApiResponse<Object> body = ApiResponse.builder()
                .success(false)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(message)
                .data(null)
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
