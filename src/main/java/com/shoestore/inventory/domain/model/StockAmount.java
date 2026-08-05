package com.shoestore.inventory.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

/**
 * Positive quantity requested by an inventory operation.
 *
 * <p>An operation amount must always be greater than zero.</p>
 */
public final class StockAmount implements ValueObject {

    private final int value;

    private StockAmount(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                    "Stock amount must be greater than zero"
            );
        }

        this.value = value;
    }

    public static StockAmount of(int value) {
        return new StockAmount(value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof StockAmount that)) {
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
