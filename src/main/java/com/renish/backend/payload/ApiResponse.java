
package com.renish.backend.payload;

import java.time.LocalDateTime;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int statusCode;
    private Object message;
    private T data;
    private String error;
    private String path;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(
            int statusCode, Object message, T data) {

        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .path(getCurrentPath())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(
            int statusCode,
            Object message,
            String error
            ) {

        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .data(null)
                .error(error)
                .path(getCurrentPath())
                .timestamp(LocalDateTime.now())
                .build();
    }
    private static String getCurrentPath(){
        var attributes = RequestContextHolder.getRequestAttributes();

        if(attributes instanceof ServletRequestAttributes servletRequestAttributes){
            HttpServletRequest request = servletRequestAttributes.getRequest();
            return  request.getRequestURI();
        }
        return null;
    }
}
