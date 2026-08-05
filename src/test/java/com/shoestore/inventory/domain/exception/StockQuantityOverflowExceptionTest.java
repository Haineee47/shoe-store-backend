package com.shoestore.inventory.domain.exception;

import com.shoestore.shared.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StockQuantityOverflowExceptionTest {

    @Test
    void shouldDescribeCurrentQuantityAndRequestedIncrease() {
        StockQuantityOverflowException exception =
                new StockQuantityOverflowException(
                        Integer.MAX_VALUE,
                        1
                );

        assertThat(exception)
                .isInstanceOf(
                        BusinessRuleViolationException.class
                );

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Stock quantity overflow: current "
                                + Integer.MAX_VALUE
                                + ", requested increase 1"
                );
    }
}
