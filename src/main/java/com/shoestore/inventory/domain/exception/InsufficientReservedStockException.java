package com.shoestore.inventory.domain.exception;

import com.shoestore.shared.domain.exception.BusinessRuleViolationException;

/**
 * Raised when attempting to release more stock than is currently reserved.
 */
public final class InsufficientReservedStockException
        extends BusinessRuleViolationException {

    public InsufficientReservedStockException(
            int requested,
            int reserved
    ) {
        super(
                "Insufficient reserved stock: requested "
                        + requested
                        + ", reserved "
                        + reserved
        );
    }
}
