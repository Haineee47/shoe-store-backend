package com.shoestore.service;

import com.shoestore.entity.RefreshToken;
import com.shoestore.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeAllUserTokens(User user);

    void revokeToken(String token);
}