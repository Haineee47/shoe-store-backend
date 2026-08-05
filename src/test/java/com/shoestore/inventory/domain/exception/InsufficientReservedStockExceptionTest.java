package com.shoestore.inventory.domain.exception;

import com.shoestore.shared.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsufficientReservedStockExceptionTest {

    @Test
    void shouldDescribeRequestedAndReservedStock() {
        InsufficientReservedStockException exception =
                new InsufficientReservedStockException(5, 4);

        assertThat(exception)
                .isInstanceOf(
                        BusinessRuleViolationException.class
                );

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Insufficient reserved stock: requested 5, reserved 4"
                );
    }
}
