package com.shoestore.shared.application.error;

import java.util.Objects;

/**
 * Base runtime exception for failures represented by an application error code.
 *
 * <p>This exception remains independent of HTTP, persistence and presentation
 * technologies.</p>
 */
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        this(errorCode, errorCodeDefaultMessage(errorCode), null);
    }

    public ApplicationException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public ApplicationException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, errorCodeDefaultMessage(errorCode), cause);
    }

    public ApplicationException(
            ErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        super(requireNonBlank(message, "message"), cause);
        this.errorCode = Objects.requireNonNull(
                errorCode,
                "errorCode must not be null"
        );
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    private static String errorCodeDefaultMessage(ErrorCode errorCode) {
        ErrorCode validatedErrorCode = Objects.requireNonNull(
                errorCode,
                "errorCode must not be null"
        );

        return requireNonBlank(
                validatedErrorCode.defaultMessage(),
                "errorCode.defaultMessage"
        );
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return value;
    }
}
