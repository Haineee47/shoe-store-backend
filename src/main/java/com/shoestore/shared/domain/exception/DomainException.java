package com.shoestore.shared.domain.exception;

/**
 * Base type for exceptions caused by violations of domain rules.
 *
 * <p>This exception belongs exclusively to the domain layer. It must not
 * contain HTTP status codes, persistence details, framework annotations,
 * localized presentation messages, or application-layer error contracts.</p>
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(requireMessage(message));
    }

    protected DomainException(String message, Throwable cause) {
        super(requireMessage(message), cause);
    }

    private static String requireMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException(
                    "Domain exception message must not be null"
            );
        }

        if (message.isBlank()) {
            throw new IllegalArgumentException(
                    "Domain exception message must not be blank"
            );
        }

        return message;
    }
}
