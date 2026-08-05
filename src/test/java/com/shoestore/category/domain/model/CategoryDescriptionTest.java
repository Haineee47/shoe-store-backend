package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CategoryDescriptionTest {

    @Test
    void shouldCreateCategoryDescription() {
        CategoryDescription description =
                CategoryDescription.of("Shoes designed for running.");

        assertThat(description.value())
                .isEqualTo("Shoes designed for running.");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        CategoryDescription description =
                CategoryDescription.of(
                        "  Shoes designed for running.  "
                );

        assertThat(description.value())
                .isEqualTo("Shoes designed for running.");
    }

    @Test
    void shouldCreateEmptyDescription() {
        CategoryDescription description =
                CategoryDescription.empty();

        assertThat(description.value()).isEmpty();
        assertThat(description.isEmpty()).isTrue();
    }

    @Test
    void shouldTreatBlankDescriptionAsEmpty() {
        CategoryDescription description =
                CategoryDescription.of("   ");

        assertThat(description.value()).isEmpty();
        assertThat(description.isEmpty()).isTrue();
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> CategoryDescription.of(null))
                .withMessage(
                        "Category description must not be null"
                );
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        CategoryDescription first =
                CategoryDescription.of("Running shoes");
        CategoryDescription second =
                CategoryDescription.of("Running shoes");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        CategoryDescription first =
                CategoryDescription.of("Running shoes");
        CategoryDescription second =
                CategoryDescription.of("Basketball shoes");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnValueAsStringRepresentation() {
        CategoryDescription description =
                CategoryDescription.of("Running shoes");

        assertThat(description.toString())
                .isEqualTo("Running shoes");
    }

    @Test
    void shouldImplementValueObjectContract() {
        CategoryDescription description =
                CategoryDescription.empty();

        assertThat(description)
                .isInstanceOf(ValueObject.class);
    }
}
