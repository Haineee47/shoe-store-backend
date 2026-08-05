package com.shoestore.inventory.domain.exception;

import com.shoestore.shared.domain.exception.BusinessRuleViolationException;

/**
 * Raised when increasing stock would exceed the supported quantity range.
 */
public final class StockQuantityOverflowException
        extends BusinessRuleViolationException {

    public StockQuantityOverflowException(
            int currentQuantity,
            int requestedIncrease
    ) {
        super(
                "Stock quantity overflow: current "
                        + currentQuantity
                        + ", requested increase "
                        + requestedIncrease
        );
    }
}
