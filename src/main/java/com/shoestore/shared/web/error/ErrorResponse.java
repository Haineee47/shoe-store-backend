package com.shoestore.shared.web.error;

import com.shoestore.shared.application.error.ErrorCode;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Stable HTTP error response contract exposed to API clients.
 *
 * <p>This type belongs to the web boundary. Application and domain code must
 * not depend on it.</p>
 *
 * @param timestamp     time when the error response was created
 * @param status        HTTP status code
 * @param code          stable machine-readable error code
 * @param message       safe human-readable message
 * @param path          request path without sensitive query information
 * @param correlationId identifier used to correlate request and logs
 * @param violations    field validation failures, empty for non-validation errors
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String correlationId,
        List<FieldViolation> violations
) {

    private static final int MINIMUM_ERROR_STATUS = 400;
    private static final int MAXIMUM_ERROR_STATUS = 599;

    public ErrorResponse {
        timestamp = Objects.requireNonNull(
                timestamp,
                "timestamp must not be null"
        );

        validateErrorStatus(status);

        code = requireNonBlank(code, "code");
        message = requireNonBlank(message, "message");
        path = requireValidPath(path);
        correlationId = requireNonBlank(
                correlationId,
                "correlationId"
        );

        violations = violations == null
                ? List.of()
                : List.copyOf(violations);
    }

    /**
     * Creates an error response from an application error code.
     *
     * @param timestamp     response creation time
     * @param status        HTTP error status
     * @param errorCode     application error code
     * @param message       safe contextual message
     * @param path          request path
     * @param correlationId request correlation identifier
     * @param violations    field validation errors
     * @return immutable error response
     */
    public static ErrorResponse of(
            Instant timestamp,
            int status,
            ErrorCode errorCode,
            String message,
            String path,
            String correlationId,
            List<FieldViolation> violations
    ) {
        ErrorCode validatedErrorCode = Objects.requireNonNull(
                errorCode,
                "errorCode must not be null"
        );

        return new ErrorResponse(
                timestamp,
                status,
                validatedErrorCode.code(),
                message,
                path,
                correlationId,
                violations
        );
    }

    /**
     * Creates an error response using the error code's default message.
     *
     * @param timestamp     response creation time
     * @param status        HTTP error status
     * @param errorCode     application error code
     * @param path          request path
     * @param correlationId request correlation identifier
     * @return immutable error response
     */
    public static ErrorResponse of(
            Instant timestamp,
            int status,
            ErrorCode errorCode,
            String path,
            String correlationId
    ) {
        ErrorCode validatedErrorCode = Objects.requireNonNull(
                errorCode,
                "errorCode must not be null"
        );

        return of(
                timestamp,
                status,
                validatedErrorCode,
                validatedErrorCode.defaultMessage(),
                path,
                correlationId,
                List.of()
        );
    }

    /**
     * Returns whether the response contains field validation failures.
     *
     * @return true when at least one field violation exists
     */
    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    private static void validateErrorStatus(int status) {
        if (status < MINIMUM_ERROR_STATUS
                || status > MAXIMUM_ERROR_STATUS) {
            throw new IllegalArgumentException(
                    "status must be between 400 and 599"
            );
        }
    }

    private static String requireValidPath(String path) {
        String validatedPath = requireNonBlank(path, "path");

        if (!validatedPath.startsWith("/")) {
            throw new IllegalArgumentException(
                    "path must start with '/'"
            );
        }

        return validatedPath;
    }

    private static String requireNonBlank(
            String value,
            String fieldName
    ) {
        Objects.requireNonNull(
                value,
                fieldName + " must not be null"
        );

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value;
    }
}
