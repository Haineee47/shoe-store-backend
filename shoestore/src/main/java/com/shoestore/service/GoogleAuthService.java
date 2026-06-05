package com.shoestore.service;

import com.shoestore.dto.response.GoogleUserInfo;

public interface GoogleAuthService {

    GoogleUserInfo verifyToken(
            String idToken
    );
}