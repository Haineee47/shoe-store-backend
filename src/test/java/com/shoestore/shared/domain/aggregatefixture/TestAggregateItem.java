package com.shoestore.shared.domain.aggregatefixture;

import com.shoestore.shared.domain.model.DomainEntity;

final class TestAggregateItem
        extends DomainEntity<TestChildId> {

    private int quantity;

    TestAggregateItem(
            TestChildId id,
            int quantity
    ) {
        super(id);
        this.quantity = requirePositiveQuantity(quantity);
    }

    int quantity() {
        return quantity;
    }

    void increaseBy(int additionalQuantity) {
        quantity = Math.addExact(
                quantity,
                requirePositiveQuantity(additionalQuantity)
        );
    }

    private static int requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        return quantity;
    }
}
