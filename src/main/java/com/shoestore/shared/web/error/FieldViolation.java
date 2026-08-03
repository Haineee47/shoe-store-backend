package com.shoestore.shared.web.error;

import java.util.Objects;

/**
 * Describes one invalid request field.
 *
 * <p>The rejected value is intentionally excluded because it may contain
 * credentials, tokens, payment information or personal data.</p>
 *
 * @param field   request field that failed validation
 * @param message safe validation message
 */
public record FieldViolation(
        String field,
        String message
) {

    public FieldViolation {
        field = requireNonBlank(field, "field");
        message = requireNonBlank(message, "message");
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value;
    }
}
