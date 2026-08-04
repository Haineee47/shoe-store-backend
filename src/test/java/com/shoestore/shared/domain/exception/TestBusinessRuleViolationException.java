package com.shoestore.shared.domain.exception;

final class TestBusinessRuleViolationException
        extends BusinessRuleViolationException {

    TestBusinessRuleViolationException(String message) {
        super(message);
    }

    TestBusinessRuleViolationException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
