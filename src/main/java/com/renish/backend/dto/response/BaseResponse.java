package com.renish.backend.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseResponse {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String lastModifiedBy;
    private Long version;
}