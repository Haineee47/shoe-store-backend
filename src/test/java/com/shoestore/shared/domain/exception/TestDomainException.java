package com.shoestore.shared.domain.exception;

final class TestDomainException extends DomainException {

    TestDomainException(String message) {
        super(message);
    }

    TestDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
