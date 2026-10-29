package com.renish.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;


    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;


    private LocalDateTime deletedAt;

    private String deletedBy;

    @Version
    @Builder.Default
    private Long version=0L;


    public void markAsDeleted(String deletedByUsername) {
        this.deleted = true;
        this.deletedBy = deletedByUsername;
        this.deletedAt = LocalDateTime.now();
    }

    public void restoreDeleted(String restoredByUsername) {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
        this.lastModifiedBy = restoredByUsername;
    }

}
