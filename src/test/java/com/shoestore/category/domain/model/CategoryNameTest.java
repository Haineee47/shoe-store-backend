package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CategoryNameTest {

    @Test
    void shouldCreateCategoryName() {
        CategoryName name = CategoryName.of("Running Shoes");

        assertThat(name.value()).isEqualTo("Running Shoes");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        CategoryName name = CategoryName.of("  Running Shoes  ");

        assertThat(name.value()).isEqualTo("Running Shoes");
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> CategoryName.of(null))
                .withMessage("Category name must not be null");
    }

    @Test
    void shouldRejectEmptyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> CategoryName.of(""))
                .withMessage("Category name must not be blank");
    }

    @Test
    void shouldRejectWhitespaceOnlyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> CategoryName.of("   "))
                .withMessage("Category name must not be blank");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        CategoryName first = CategoryName.of("Running Shoes");
        CategoryName second = CategoryName.of("Running Shoes");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        CategoryName first = CategoryName.of("Running Shoes");
        CategoryName second = CategoryName.of("Basketball Shoes");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldPreserveCase() {
        CategoryName first = CategoryName.of("Running Shoes");
        CategoryName second = CategoryName.of("running shoes");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnValueAsStringRepresentation() {
        CategoryName name = CategoryName.of("Running Shoes");

        assertThat(name.toString()).isEqualTo("Running Shoes");
    }

    @Test
    void shouldImplementValueObjectContract() {
        CategoryName name = CategoryName.of("Running Shoes");

        assertThat(name).isInstanceOf(ValueObject.class);
    }
}
