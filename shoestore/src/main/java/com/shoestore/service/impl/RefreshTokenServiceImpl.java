package com.shoestore.service.impl;

import com.shoestore.config.JwtProperties;
import com.shoestore.entity.RefreshToken;
import com.shoestore.entity.User;
import com.shoestore.repository.RefreshTokenRepository;
import com.shoestore.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .user(user)
                        .expiredAt(
                                LocalDateTime.now()
                                        .plusSeconds(
                                                jwtProperties
                                                        .getRefreshTokenExpiration()
                                                        / 1000
                                        )
                        )
                        .revoked(false)
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"));

        if (refreshToken.getRevoked()) {
            throw new RuntimeException(
                    "Refresh token revoked");
        }

        if (refreshToken.getExpiredAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token expired");
        }

        return refreshToken;
    }

    @Override
    public void revokeAllUserTokens(User user) {

        refreshTokenRepository
                .findAllByUser(user)
                .forEach(token -> {

                    token.setRevoked(true);

                    refreshTokenRepository.save(token);
                });
    }

    @Override
    public void revokeToken(String token) {

        RefreshToken refreshToken =
                verifyRefreshToken(token);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}