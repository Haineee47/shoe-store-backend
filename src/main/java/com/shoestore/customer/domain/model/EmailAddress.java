package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Locale;
import java.util.Objects;

/**
 * Contact email address of a customer.
 *
 * <p>This type performs conservative structural validation without
 * attempting to implement the complete email-address specification.
 * The domain part is normalized to lowercase while the local part is
 * preserved.</p>
 *
 * <p>This value represents customer contact information. It does not
 * represent an authentication credential or verified login identity.</p>
 */
public final class EmailAddress implements ValueObject {

    private final String value;

    private EmailAddress(String value) {
        this.value = normalize(value);
    }

    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }

    public String value() {
        return value;
    }

    private static String normalize(String value) {
        Objects.requireNonNull(
                value,
                "Email address must not be null"
        );

        String strippedValue = value.strip();

        if (strippedValue.isEmpty()) {
            throw new IllegalArgumentException(
                    "Email address must not be blank"
            );
        }

        if (containsWhitespace(strippedValue)) {
            throw invalidFormat();
        }

        int separatorIndex = strippedValue.indexOf('@');

        if (separatorIndex <= 0
                || separatorIndex
                != strippedValue.lastIndexOf('@')
                || separatorIndex
                == strippedValue.length() - 1) {
            throw invalidFormat();
        }

        String localPart =
                strippedValue.substring(0, separatorIndex);

        String domainPart =
                strippedValue.substring(separatorIndex + 1);

        validateLocalPart(localPart);
        validateDomainPart(domainPart);

        return localPart
                + "@"
                + domainPart.toLowerCase(Locale.ROOT);
    }

    private static void validateLocalPart(String localPart) {
        if (localPart.startsWith(".")
                || localPart.endsWith(".")
                || localPart.contains("..")) {
            throw invalidFormat();
        }
    }

    private static void validateDomainPart(String domainPart) {
        if (domainPart.startsWith(".")
                || domainPart.endsWith(".")
                || domainPart.startsWith("-")
                || domainPart.endsWith("-")
                || domainPart.contains("..")
                || !domainPart.contains(".")) {
            throw invalidFormat();
        }

        for (int index = 0; index < domainPart.length(); index++) {
            char character = domainPart.charAt(index);

            boolean supported =
                    Character.isLetterOrDigit(character)
                            || character == '.'
                            || character == '-';

            if (!supported) {
                throw invalidFormat();
            }
        }
    }

    private static boolean containsWhitespace(String value) {
        return value.chars()
                .anyMatch(Character::isWhitespace);
    }

    private static IllegalArgumentException invalidFormat() {
        return new IllegalArgumentException(
                "Email address format is invalid"
        );
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof EmailAddress that)) {
            return false;
        }

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
