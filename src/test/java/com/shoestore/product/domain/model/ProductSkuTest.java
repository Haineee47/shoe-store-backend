package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ProductSkuTest {

    @Test
    void shouldCreateProductSku() {
        ProductSku sku = ProductSku.of("NIKE-AIR-MAX");

        assertThat(sku.value()).isEqualTo("NIKE-AIR-MAX");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        ProductSku sku = ProductSku.of("  NIKE-AIR-MAX  ");

        assertThat(sku.value()).isEqualTo("NIKE-AIR-MAX");
    }

    @Test
    void shouldNormalizeValueToUppercase() {
        ProductSku sku = ProductSku.of("nike-air-max");

        assertThat(sku.value()).isEqualTo("NIKE-AIR-MAX");
    }

    @Test
    void shouldAllowLettersDigitsHyphensAndUnderscores() {
        ProductSku sku =
                ProductSku.of("nike_air-max_2026");

        assertThat(sku.value())
                .isEqualTo("NIKE_AIR-MAX_2026");
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> ProductSku.of(null))
                .withMessage("Product SKU must not be null");
    }

    @Test
    void shouldRejectEmptyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductSku.of(""))
                .withMessage("Product SKU must not be blank");
    }

    @Test
    void shouldRejectWhitespaceOnlyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductSku.of("   "))
                .withMessage("Product SKU must not be blank");
    }

    @Test
    void shouldRejectInternalWhitespace() {
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> ProductSku.of("NIKE AIR MAX")
                )
                .withMessage(
                        "Product SKU must contain only letters, digits, hyphens, or underscores"
                );
    }

    @Test
    void shouldRejectUnsupportedCharacters() {
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> ProductSku.of("NIKE/AIR/MAX")
                )
                .withMessage(
                        "Product SKU must contain only letters, digits, hyphens, or underscores"
                );
    }

    @Test
    void shouldBeEqualAfterNormalization() {
        ProductSku first =
                ProductSku.of("nike-air-max");
        ProductSku second =
                ProductSku.of("NIKE-AIR-MAX");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        ProductSku first =
                ProductSku.of("NIKE-AIR-MAX");
        ProductSku second =
                ProductSku.of("ADIDAS-ULTRABOOST");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnNormalizedValueAsStringRepresentation() {
        ProductSku sku =
                ProductSku.of("nike-air-max");

        assertThat(sku.toString())
                .isEqualTo("NIKE-AIR-MAX");
    }

    @Test
    void shouldImplementValueObjectContract() {
        ProductSku sku =
                ProductSku.of("NIKE-AIR-MAX");

        assertThat(sku)
                .isInstanceOf(ValueObject.class);
    }
}
