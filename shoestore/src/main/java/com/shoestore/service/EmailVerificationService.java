package com.shoestore.service;

import com.shoestore.entity.User;

public interface EmailVerificationService {

    void createAndSendVerificationToken(User user);

    void verifyEmail(String token);

    void resendVerificationEmail(String email);
}