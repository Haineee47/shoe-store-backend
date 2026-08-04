package com.shoestore.shared.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DomainExceptionTest {

    @Test
    void shouldPreserveDomainMessage() {
        TestDomainException exception =
                new TestDomainException("Product price must be positive");

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product price must be positive");
    }

    @Test
    void shouldPreserveCause() {
        RuntimeException cause =
                new RuntimeException("Original failure");

        TestDomainException exception =
                new TestDomainException(
                        "Domain operation failed",
                        cause
                );

        assertThat(exception)
                .hasMessage("Domain operation failed")
                .hasCause(cause);
    }

    @Test
    void shouldRejectNullMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TestDomainException(null))
                .withMessage(
                        "Domain exception message must not be null"
                );
    }

    @Test
    void shouldRejectBlankMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TestDomainException("   "))
                .withMessage(
                        "Domain exception message must not be blank"
                );
    }
}
