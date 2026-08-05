package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ProductIdTest {

    @Test
    void shouldGenerateProductId() {
        ProductId productId = ProductId.generate();

        assertThat(productId).isNotNull();
        assertThat(productId.value()).isNotNull();
    }

    @Test
    void shouldCreateProductIdFromUuid() {
        UUID value = UUID.randomUUID();

        ProductId productId = ProductId.from(value);

        assertThat(productId.value()).isEqualTo(value);
    }

    @Test
    void shouldRejectNullUuid() {
        assertThatNullPointerException()
                .isThrownBy(() -> ProductId.from(null))
                .withMessage("Product id value must not be null");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        UUID value = UUID.randomUUID();

        ProductId first = ProductId.from(value);
        ProductId second = ProductId.from(value);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        ProductId first = ProductId.generate();
        ProductId second = ProductId.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExposeUuidAsStringRepresentation() {
        UUID value = UUID.randomUUID();

        ProductId productId = ProductId.from(value);

        assertThat(productId.toString())
                .isEqualTo(value.toString());
    }

    @Test
    void shouldImplementValueObjectContract() {
        ProductId productId = ProductId.generate();

        assertThat(productId)
                .isInstanceOf(ValueObject.class);
    }

    @Test
    void shouldBeSerializable() {
        ProductId productId = ProductId.generate();

        assertThat(productId)
                .isInstanceOf(Serializable.class);
    }
}
