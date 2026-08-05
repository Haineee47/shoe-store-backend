package com.shoestore.inventory.domain.exception;

import com.shoestore.shared.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsufficientAvailableStockExceptionTest {

    @Test
    void shouldDescribeRequestedAndAvailableStock() {
        InsufficientAvailableStockException exception =
                new InsufficientAvailableStockException(4, 3);

        assertThat(exception)
                .isInstanceOf(
                        BusinessRuleViolationException.class
                );

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Insufficient available stock: requested 4, available 3"
                );
    }
}
