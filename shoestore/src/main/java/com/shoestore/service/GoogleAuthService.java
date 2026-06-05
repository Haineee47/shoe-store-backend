package com.shoestore.service;

import com.shoestore.dto.response.authResponse.GoogleUserInfo;

public interface GoogleAuthService {

    GoogleUserInfo verifyToken(
            String idToken
    );
}