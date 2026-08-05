package com.shoestore.inventory.domain.exception;

import com.shoestore.shared.domain.exception.BusinessRuleViolationException;

/**
 * Raised when an inventory operation requests more stock than is currently
 * available.
 */
public final class InsufficientAvailableStockException
        extends BusinessRuleViolationException {

    public InsufficientAvailableStockException(
            int requested,
            int available
    ) {
        super(
                "Insufficient available stock: requested "
                        + requested
                        + ", available "
                        + available
        );
    }
}
