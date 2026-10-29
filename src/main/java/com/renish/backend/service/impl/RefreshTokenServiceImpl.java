package com.renish.backend.service.impl;

import com.renish.backend.entity.RefreshToken;
import com.renish.backend.entity.Student;
import com.renish.backend.exception.InvalidTokenException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.exception.TokenExpiredException;
import com.renish.backend.exception.TokenRevokedException;
import com.renish.backend.repository.RefreshTokenRepository;
import com.renish.backend.repository.StudentRepository;
import com.renish.backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final StudentRepository studentRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh.expiration-days}")
    private Long refreshTokenExpiryDays;

    public RefreshToken createRefreshToken(String email){
        Student student = studentRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Student not found with email: "+email));
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .student(student)
                .expiryDate(LocalDateTime.now().plusDays(refreshTokenExpiryDays))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);

    }
    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(()->new InvalidTokenException("Invalid refresh token"));

        if(refreshToken.isRevoked()){
            throw new TokenRevokedException("Refresh token has already been revoked");
        }
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Refresh token has been expired");
        }
        return refreshToken;
    }


    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken){
      oldToken.setRevoked(true);
      refreshTokenRepository.save(oldToken);
      return createRefreshToken(oldToken.getStudent().getEmail());
    }

    @Transactional
    public void revokeRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(()->new InvalidTokenException("Invalid refresh token"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
