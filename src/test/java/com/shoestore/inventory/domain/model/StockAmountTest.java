package com.shoestore.inventory.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class StockAmountTest {

    @Test
    void shouldCreatePositiveStockAmount() {
        StockAmount amount = StockAmount.of(5);

        assertThat(amount.value()).isEqualTo(5);
    }

    @Test
    void shouldAllowOneAsMinimumAmount() {
        StockAmount amount = StockAmount.of(1);

        assertThat(amount.value()).isEqualTo(1);
    }

    @Test
    void shouldRejectZeroAmount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StockAmount.of(0))
                .withMessage(
                        "Stock amount must be greater than zero"
                );
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StockAmount.of(-1))
                .withMessage(
                        "Stock amount must be greater than zero"
                );
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        StockAmount first = StockAmount.of(5);
        StockAmount second = StockAmount.of(5);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        StockAmount first = StockAmount.of(5);
        StockAmount second = StockAmount.of(10);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnValueAsStringRepresentation() {
        StockAmount amount = StockAmount.of(5);

        assertThat(amount.toString()).isEqualTo("5");
    }

    @Test
    void shouldImplementValueObjectContract() {
        StockAmount amount = StockAmount.of(5);

        assertThat(amount).isInstanceOf(ValueObject.class);
    }
}
