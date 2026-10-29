package com.renish.backend.payload;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data 
@AllArgsConstructor  
@Builder 
public class ErrorResponse {
    private boolean success;
    private int statusCode;
    private Object message;
    private String error;
    private String path;
    private LocalDateTime timestamp;
}
