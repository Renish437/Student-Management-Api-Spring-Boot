package com.renish.backend.dto.response.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AdminBaseResponse {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String lastModifiedBy;
    private Long version;
    private boolean deleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
}
