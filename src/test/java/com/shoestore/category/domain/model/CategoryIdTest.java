package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CategoryIdTest {

    @Test
    void shouldGenerateCategoryId() {
        CategoryId categoryId = CategoryId.generate();

        assertThat(categoryId).isNotNull();
        assertThat(categoryId.value()).isNotNull();
    }

    @Test
    void shouldCreateCategoryIdFromUuid() {
        UUID value = UUID.randomUUID();

        CategoryId categoryId = CategoryId.from(value);

        assertThat(categoryId.value()).isEqualTo(value);
    }

    @Test
    void shouldRejectNullUuid() {
        assertThatNullPointerException()
                .isThrownBy(() -> CategoryId.from(null))
                .withMessage("Category id value must not be null");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        UUID value = UUID.randomUUID();

        CategoryId first = CategoryId.from(value);
        CategoryId second = CategoryId.from(value);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        CategoryId first = CategoryId.generate();
        CategoryId second = CategoryId.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExposeUuidAsStringRepresentation() {
        UUID value = UUID.randomUUID();

        CategoryId categoryId = CategoryId.from(value);

        assertThat(categoryId.toString())
                .isEqualTo(value.toString());
    }

    @Test
    void shouldImplementValueObjectContract() {
        CategoryId categoryId = CategoryId.generate();

        assertThat(categoryId).isInstanceOf(ValueObject.class);
    }

    @Test
    void shouldBeSerializable() {
        CategoryId categoryId = CategoryId.generate();

        assertThat(categoryId).isInstanceOf(Serializable.class);
    }
}
