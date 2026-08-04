package com.shoestore.shared.domain.service;

import com.shoestore.shared.domain.model.ValueObject;

record TestDiscountPolicy(
        int minimumQuantity,
        int percentage
) implements ValueObject {

    TestDiscountPolicy {
        if (minimumQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Minimum quantity must be positive"
            );
        }

        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Discount percentage must be between 0 and 100"
            );
        }
    }

    boolean appliesTo(int quantity) {
        return quantity >= minimumQuantity;
    }
}
