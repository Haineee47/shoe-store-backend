package com.shoestore.shared.application.error;

import java.util.Objects;

/**
 * Error codes that describe technical or application-wide failures.
 *
 * <p>Business-specific error codes must be owned by their respective
 * business modules and must not be added to this enum.</p>
 */
public enum CommonErrorCode implements ErrorCode {

    INVALID_REQUEST(
            "COMMON_INVALID_REQUEST",
            "The request is invalid."
    ),

    INTERNAL_ERROR(
            "COMMON_INTERNAL_ERROR",
            "An unexpected error occurred."
    );

    private final String code;
    private final String defaultMessage;

    CommonErrorCode(String code, String defaultMessage) {
        this.code = requireNonBlank(code, "code");
        this.defaultMessage = requireNonBlank(defaultMessage, "defaultMessage");
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return value;
    }
}
