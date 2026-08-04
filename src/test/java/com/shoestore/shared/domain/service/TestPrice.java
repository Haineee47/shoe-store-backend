package com.shoestore.shared.domain.service;

import com.shoestore.shared.domain.model.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

record TestPrice(BigDecimal amount) implements ValueObject {

    private static final int SCALE = 2;

    TestPrice {
        if (amount == null) {
            throw new IllegalArgumentException(
                    "Price amount must not be null"
            );
        }

        if (amount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Price amount must not be negative"
            );
        }

        amount = amount.setScale(SCALE, RoundingMode.UNNECESSARY);
    }

    TestPrice subtract(TestPrice other) {
        if (other == null) {
            throw new IllegalArgumentException(
                    "Price to subtract must not be null"
            );
        }

        BigDecimal result = amount.subtract(other.amount);

        if (result.signum() < 0) {
            throw new IllegalArgumentException(
                    "Price result must not be negative"
            );
        }

        return new TestPrice(result);
    }

    TestPrice percentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Percentage must be between 0 and 100"
            );
        }

        BigDecimal calculated = amount
                .multiply(BigDecimal.valueOf(percentage))
                .divide(
                        BigDecimal.valueOf(100),
                        SCALE,
                        RoundingMode.HALF_UP
                );

        return new TestPrice(calculated);
    }
}
