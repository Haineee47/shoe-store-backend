package com.shoestore.inventory.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

/**
 * Non-negative quantity representing inventory state.
 *
 * <p>A stock quantity may be zero but must never be negative.</p>
 */
public final class StockQuantity implements ValueObject {

    private static final StockQuantity ZERO =
            new StockQuantity(0);

    private final int value;

    private StockQuantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must not be negative"
            );
        }

        this.value = value;
    }

    public static StockQuantity of(int value) {
        if (value == 0) {
            return ZERO;
        }

        return new StockQuantity(value);
    }

    public static StockQuantity zero() {
        return ZERO;
    }

    public int value() {
        return value;
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean isPositive() {
        return value > 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof StockQuantity that)) {
            return false;
        }

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
