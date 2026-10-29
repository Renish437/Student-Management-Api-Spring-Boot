package com.renish.backend.service.impl;

import com.renish.backend.entity.OtpVerification;
import com.renish.backend.exception.*;
import com.renish.backend.repository.OtpVerificationRepository;
import com.renish.backend.service.EmailService;
import com.renish.backend.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpVerificationRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;

    private static  final int MAX_ATTEMPTS=3;
    @Override
    public void generateAndSendOtp(String email) {
     String otp = String.format(
             "%06d",secureRandom.nextInt(1_00_000)
     );
        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otp(passwordEncoder.encode(otp))
                .expiresAt(
                        LocalDateTime.now().plusMinutes(5)
                )
                .attempts(0)
                .verified(false)
                .build();
        otpVerificationRepository.save(otpVerification);
        emailService.sendOtpEmail(
                email,otp
        );

    }

    @Override
    @Transactional(noRollbackFor = {
            InvalidOtpException.class,
            AttemptCountExceededException.class
    })
    public void verifyOtp(String email, String otp) {

        OtpVerification otpVerification = otpVerificationRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("OTP not found")
                );

        if (otpVerification.isVerified()) {
            throw new OtpAlreadyVerifiedException(
                    "OTP has already been verified."
            );
        }

        if (!LocalDateTime.now().isBefore(otpVerification.getExpiresAt())) {
            throw new OtpExpiredException("OTP has expired.");
        }

        if (otpVerification.getAttempts() >= MAX_ATTEMPTS) {
            throw new AttemptCountExceededException(
                    "Maximum OTP attempts exceeded."
            );
        }

        if (!passwordEncoder.matches(otp, otpVerification.getOtp())) {

            otpVerification.setAttempts(
                    otpVerification.getAttempts() + 1
            );

            otpVerificationRepository.saveAndFlush(otpVerification);

            if (otpVerification.getAttempts() >= MAX_ATTEMPTS) {
                throw new AttemptCountExceededException(
                        "Maximum OTP attempts exceeded."
                );
            }

            throw new InvalidOtpException(
                    "Invalid OTP. Please try again."
            );
        }

        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);
    }
}
