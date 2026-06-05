package com.shoestore.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.config.GoogleProperties;
import com.shoestore.dto.response.authResponse.GoogleUserInfo;
import com.shoestore.exception.BusinessException;
import com.shoestore.service.GoogleAuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl
        implements GoogleAuthService {

    private final GoogleProperties googleProperties;

    @Override
    public GoogleUserInfo verifyToken(
            String idToken
    ) {

        try {

            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance()
                    )
                            .setAudience(
                                    Collections.singletonList(
                                            googleProperties.getClientId()
                                    )
                            )
                            .build();

            GoogleIdToken googleIdToken =
                    verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new BusinessException(
                        ErrorCode.GOOGLE_TOKEN_INVALID
                );
            }

            GoogleIdToken.Payload payload =
                    googleIdToken.getPayload();

            return GoogleUserInfo.builder()
                    .googleId(payload.getSubject())
                    .email(payload.getEmail())
                    .name((String) payload.get("name"))
                    .picture((String) payload.get("picture"))
                    .emailVerified(payload.getEmailVerified())
                    .build();

        } catch (Exception ex) {

            throw new BusinessException(
                    ErrorCode.GOOGLE_AUTHENTICATION_FAILED
            );
        }
    }
}