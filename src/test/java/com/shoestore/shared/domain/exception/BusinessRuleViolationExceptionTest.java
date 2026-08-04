package com.shoestore.shared.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class BusinessRuleViolationExceptionTest {

    @Test
    void shouldBeADomainException() {
        TestBusinessRuleViolationException exception =
                new TestBusinessRuleViolationException(
                        "Product price must be positive"
                );

        assertThat(exception)
                .isInstanceOf(DomainException.class)
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldPreserveBusinessRuleMessage() {
        TestBusinessRuleViolationException exception =
                new TestBusinessRuleViolationException(
                        "Product price must be positive"
                );

        assertThat(exception)
                .hasMessage("Product price must be positive");
    }

    @Test
    void shouldPreserveCause() {
        RuntimeException cause =
                new RuntimeException("Original failure");

        TestBusinessRuleViolationException exception =
                new TestBusinessRuleViolationException(
                        "Business rule evaluation failed",
                        cause
                );

        assertThat(exception)
                .hasMessage("Business rule evaluation failed")
                .hasCause(cause);
    }

    @Test
    void shouldRejectNullMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> new TestBusinessRuleViolationException(null)
                )
                .withMessage(
                        "Domain exception message must not be null"
                );
    }

    @Test
    void shouldRejectBlankMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> new TestBusinessRuleViolationException("   ")
                )
                .withMessage(
                        "Domain exception message must not be blank"
                );
    }
}
