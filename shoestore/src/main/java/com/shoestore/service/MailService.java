package com.shoestore.service;

public interface MailService {

    void sendVerificationEmail(
            String to,
            String verificationUrl
    );

    void sendResetPasswordEmail(String toEmail, String resetLink);
}