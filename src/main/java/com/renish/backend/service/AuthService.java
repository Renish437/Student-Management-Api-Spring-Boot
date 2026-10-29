package com.renish.backend.service;

import com.renish.backend.dto.request.LoginRequest;
import com.renish.backend.dto.response.LoginResponse;

public interface AuthService {
    public LoginResponse refreshToken(
            String refreshToken
    );
    public LoginResponse login(LoginRequest req);
    void logout(String refreshToken);
}


