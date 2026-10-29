package com.renish.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="otp_verifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String otp;
    private LocalDateTime expiresAt;
    private boolean verified;

    @Builder.Default
    private Integer attempts=0;

}
