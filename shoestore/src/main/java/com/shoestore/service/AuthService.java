package com.shoestore.service;

import com.shoestore.dto.request.authRequest.*;
import com.shoestore.dto.response.authResponse.AuthResponse;
import com.shoestore.dto.response.authResponse.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(
            RefreshTokenRequest request
    );

    void logout(String refreshToken);

    AuthResponse googleLogin(
            GoogleLoginRequest request
    );

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    UserResponse getCurrentUser();
}