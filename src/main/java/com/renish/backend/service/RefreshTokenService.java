package com.renish.backend.service;

import com.renish.backend.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String email);
    RefreshToken verifyRefreshToken(String token);
    RefreshToken rotateRefreshToken(RefreshToken oldToken);
    void revokeRefreshToken(String token);
}
