package com.renish.backend.service.impl;

import com.renish.backend.dto.request.LoginRequest;
import com.renish.backend.dto.response.LoginResponse;
import com.renish.backend.entity.RefreshToken;
import com.renish.backend.service.AuthService;
import com.renish.backend.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final RefreshTokenServiceImpl refreshTokenService;

    @Override
    public LoginResponse login(LoginRequest req){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(),req.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();

    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }


    @Override
    public LoginResponse refreshToken(
         String refreshToken
    ){
        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(refreshToken);
        UserDetails userDetails = User.builder()
                .username(verifiedToken.getStudent().getEmail())
                .password(verifiedToken.getStudent().getPassword())
                .roles(verifiedToken.getStudent().getRole())
                .build();
        String newAccessToken = jwtService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(
                verifiedToken
        );
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();

    }

}
