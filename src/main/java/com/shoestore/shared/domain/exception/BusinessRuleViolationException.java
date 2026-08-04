package com.shoestore.shared.domain.exception;

/**
 * Base type for exceptions raised when an operation violates a domain
 * invariant or business rule.
 *
 * <p>Concrete domain exceptions should communicate the violated business
 * concept through their type and message. This class must not contain HTTP,
 * persistence, localization, or application-layer concerns.</p>
 */
public abstract class BusinessRuleViolationException
        extends DomainException {

    protected BusinessRuleViolationException(String message) {
        super(message);
    }

    protected BusinessRuleViolationException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
