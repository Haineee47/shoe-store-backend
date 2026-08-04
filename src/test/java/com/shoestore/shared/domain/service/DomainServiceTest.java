package com.shoestore.shared.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DomainServiceTest {

    private final TestPricingService pricingService =
            new TestPricingService();

    @Test
    void shouldBeRecognizedAsDomainService() {
        assertThat(pricingService)
                .isInstanceOf(DomainService.class);
    }

    @Test
    void shouldCalculateTotalWithoutDiscount() {
        TestPrice result = pricingService.calculateFinalPrice(
                new TestPrice(new BigDecimal("100.00")),
                2,
                new TestDiscountPolicy(3, 10)
        );

        assertThat(result.amount())
                .isEqualByComparingTo("200.00");
    }

    @Test
    void shouldApplyDiscountWhenPolicyMatches() {
        TestPrice result = pricingService.calculateFinalPrice(
                new TestPrice(new BigDecimal("100.00")),
                3,
                new TestDiscountPolicy(3, 10)
        );

        assertThat(result.amount())
                .isEqualByComparingTo("270.00");
    }

    @Test
    void shouldProduceSameResultForSameInput() {
        TestPrice unitPrice =
                new TestPrice(new BigDecimal("100.00"));

        TestDiscountPolicy policy =
                new TestDiscountPolicy(2, 15);

        TestPrice first = pricingService.calculateFinalPrice(
                unitPrice,
                4,
                policy
        );

        TestPrice second = pricingService.calculateFinalPrice(
                unitPrice,
                4,
                policy
        );

        assertThat(first).isEqualTo(second);
    }

    @Test
    void shouldNotMutateInputValues() {
        TestPrice unitPrice =
                new TestPrice(new BigDecimal("100.00"));

        TestDiscountPolicy policy =
                new TestDiscountPolicy(2, 10);

        pricingService.calculateFinalPrice(
                unitPrice,
                2,
                policy
        );

        assertThat(unitPrice.amount())
                .isEqualByComparingTo("100.00");

        assertThat(policy)
                .isEqualTo(new TestDiscountPolicy(2, 10));
    }

    @Test
    void shouldRejectNullUnitPrice() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        pricingService.calculateFinalPrice(
                                null,
                                1,
                                new TestDiscountPolicy(1, 10)
                        )
                )
                .withMessage("Unit price must not be null");
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        pricingService.calculateFinalPrice(
                                new TestPrice(
                                        new BigDecimal("100.00")
                                ),
                                0,
                                new TestDiscountPolicy(1, 10)
                        )
                )
                .withMessage("Quantity must be positive");
    }

    @Test
    void shouldRejectNullDiscountPolicy() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        pricingService.calculateFinalPrice(
                                new TestPrice(
                                        new BigDecimal("100.00")
                                ),
                                1,
                                null
                        )
                )
                .withMessage("Discount policy must not be null");
    }
}
