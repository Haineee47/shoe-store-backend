package com.shoestore.inventory.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class StockQuantityTest {

    @Test
    void shouldCreatePositiveStockQuantity() {
        StockQuantity quantity = StockQuantity.of(25);

        assertThat(quantity.value()).isEqualTo(25);
    }

    @Test
    void shouldCreateZeroStockQuantity() {
        StockQuantity quantity = StockQuantity.zero();

        assertThat(quantity.value()).isZero();
        assertThat(quantity.isZero()).isTrue();
        assertThat(quantity.isPositive()).isFalse();
    }

    @Test
    void shouldNormalizeZeroFactoryToZeroValue() {
        StockQuantity quantity = StockQuantity.of(0);

        assertThat(quantity).isEqualTo(StockQuantity.zero());
    }

    @Test
    void shouldIdentifyPositiveQuantity() {
        StockQuantity quantity = StockQuantity.of(1);

        assertThat(quantity.isPositive()).isTrue();
        assertThat(quantity.isZero()).isFalse();
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StockQuantity.of(-1))
                .withMessage(
                        "Stock quantity must not be negative"
                );
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        StockQuantity first = StockQuantity.of(25);
        StockQuantity second = StockQuantity.of(25);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        StockQuantity first = StockQuantity.of(25);
        StockQuantity second = StockQuantity.of(20);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnValueAsStringRepresentation() {
        StockQuantity quantity = StockQuantity.of(25);

        assertThat(quantity.toString()).isEqualTo("25");
    }

    @Test
    void shouldImplementValueObjectContract() {
        StockQuantity quantity = StockQuantity.zero();

        assertThat(quantity).isInstanceOf(ValueObject.class);
    }
}
