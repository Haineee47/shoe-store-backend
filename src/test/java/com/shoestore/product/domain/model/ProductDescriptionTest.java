package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ProductDescriptionTest {

    @Test
    void shouldCreateProductDescription() {
        ProductDescription description =
                ProductDescription.of(
                        "Lightweight running shoes."
                );

        assertThat(description.value())
                .isEqualTo("Lightweight running shoes.");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        ProductDescription description =
                ProductDescription.of(
                        "  Lightweight running shoes.  "
                );

        assertThat(description.value())
                .isEqualTo("Lightweight running shoes.");
    }

    @Test
    void shouldCreateEmptyDescription() {
        ProductDescription description =
                ProductDescription.empty();

        assertThat(description.value()).isEmpty();
        assertThat(description.isEmpty()).isTrue();
    }

    @Test
    void shouldTreatBlankDescriptionAsEmpty() {
        ProductDescription description =
                ProductDescription.of("   ");

        assertThat(description.value()).isEmpty();
        assertThat(description.isEmpty()).isTrue();
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> ProductDescription.of(null))
                .withMessage(
                        "Product description must not be null"
                );
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        ProductDescription first =
                ProductDescription.of("Running shoes");
        ProductDescription second =
                ProductDescription.of("Running shoes");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        ProductDescription first =
                ProductDescription.of("Running shoes");
        ProductDescription second =
                ProductDescription.of("Basketball shoes");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnValueAsStringRepresentation() {
        ProductDescription description =
                ProductDescription.of("Running shoes");

        assertThat(description.toString())
                .isEqualTo("Running shoes");
    }

    @Test
    void shouldImplementValueObjectContract() {
        ProductDescription description =
                ProductDescription.empty();

        assertThat(description)
                .isInstanceOf(ValueObject.class);
    }
}
