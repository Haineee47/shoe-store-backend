package com.shoestore.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS("Email already exists"),

    USER_NOT_FOUND("User not found"),

    INVALID_CREDENTIALS("Invalid credentials"),

    ACCOUNT_LOCKED("Account locked"),

    ACCOUNT_DISABLED("Account disabled"),

    ROLE_NOT_FOUND("Role not found"),

    REFRESH_TOKEN_NOT_FOUND("Refresh token not found"),

    REFRESH_TOKEN_EXPIRED("Refresh token expired"),

    REFRESH_TOKEN_REVOKED("Refresh token revoked"),

    ACCESS_DENIED("Access denied"),

    EMAIL_NOT_VERIFIED("Email not verified"),

    INVALID_TOKEN("Invalid token"),

    TOKEN_EXPIRED("Token expired"),

    TOKEN_ALREADY_USED("Token already used"),

    EMAIL_ALREADY_VERIFIED("Email already verified"),

    GOOGLE_TOKEN_INVALID("Google token invalid"),

    GOOGLE_AUTHENTICATION_FAILED("Google authentication failed"),

    GOOGLE_EMAIL_NOT_VERIFIED("Google email not verified"),

    INVALID_RESET_TOKEN("Invalid reset token"),

    RESET_TOKEN_EXPIRED("Reset token expired"),

    NEW_PASSWORD_MUST_BE_DIFFERENT("New password must be different"),

    INCORRECT_OLD_PASSWORD("Incorrect old password"),

    SOCIAL_ACCOUNT_CANNOT_CHANGE_PASSWORD("Social account cannot change password"),

    PLEASE_WAIT_BEFORE_RESENDING("Please wait before resending"),

    UNAUTHORIZED("Unauthorize"),

    INTERNAL_SERVER_ERROR("Internal server error");

    private final String message;
}