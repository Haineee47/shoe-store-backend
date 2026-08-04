package com.shoestore.shared.domain.service;

final class TestPricingService implements DomainService {

    TestPrice calculateFinalPrice(
            TestPrice unitPrice,
            int quantity,
            TestDiscountPolicy discountPolicy
    ) {
        if (unitPrice == null) {
            throw new IllegalArgumentException(
                    "Unit price must not be null"
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        if (discountPolicy == null) {
            throw new IllegalArgumentException(
                    "Discount policy must not be null"
            );
        }

        TestPrice total = new TestPrice(
                unitPrice.amount()
                        .multiply(
                                java.math.BigDecimal.valueOf(quantity)
                        )
        );

        if (!discountPolicy.appliesTo(quantity)) {
            return total;
        }

        TestPrice discount =
                total.percentage(discountPolicy.percentage());

        return total.subtract(discount);
    }
}
