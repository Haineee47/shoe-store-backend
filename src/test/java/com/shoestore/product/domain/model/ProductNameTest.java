package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ProductNameTest {

    @Test
    void shouldCreateProductName() {
        ProductName name = ProductName.of("Nike Air Max");

        assertThat(name.value()).isEqualTo("Nike Air Max");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        ProductName name = ProductName.of("  Nike Air Max  ");

        assertThat(name.value()).isEqualTo("Nike Air Max");
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> ProductName.of(null))
                .withMessage("Product name must not be null");
    }

    @Test
    void shouldRejectEmptyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductName.of(""))
                .withMessage("Product name must not be blank");
    }

    @Test
    void shouldRejectWhitespaceOnlyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductName.of("   "))
                .withMessage("Product name must not be blank");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        ProductName first = ProductName.of("Nike Air Max");
        ProductName second = ProductName.of("Nike Air Max");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        ProductName first = ProductName.of("Nike Air Max");
        ProductName second = ProductName.of("Adidas Ultraboost");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldPreserveCase() {
        ProductName first = ProductName.of("Nike Air Max");
        ProductName second = ProductName.of("nike air max");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnValueAsStringRepresentation() {
        ProductName name = ProductName.of("Nike Air Max");

        assertThat(name.toString()).isEqualTo("Nike Air Max");
    }

    @Test
    void shouldImplementValueObjectContract() {
        ProductName name = ProductName.of("Nike Air Max");

        assertThat(name).isInstanceOf(ValueObject.class);
    }
}
