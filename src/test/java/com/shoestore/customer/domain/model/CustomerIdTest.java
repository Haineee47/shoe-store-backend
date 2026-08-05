package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CustomerIdTest {

    @Test
    void shouldGenerateCustomerId() {
        CustomerId customerId = CustomerId.generate();

        assertThat(customerId).isNotNull();
        assertThat(customerId.value()).isNotNull();
    }

    @Test
    void shouldCreateCustomerIdFromUuid() {
        UUID value = UUID.randomUUID();

        CustomerId customerId = CustomerId.from(value);

        assertThat(customerId.value()).isEqualTo(value);
    }

    @Test
    void shouldRejectNullUuid() {
        assertThatNullPointerException()
                .isThrownBy(() -> CustomerId.from(null))
                .withMessage("Customer id value must not be null");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        UUID value = UUID.randomUUID();

        CustomerId first = CustomerId.from(value);
        CustomerId second = CustomerId.from(value);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        CustomerId first = CustomerId.generate();
        CustomerId second = CustomerId.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExposeUuidAsStringRepresentation() {
        UUID value = UUID.randomUUID();

        CustomerId customerId = CustomerId.from(value);

        assertThat(customerId.toString())
                .isEqualTo(value.toString());
    }

    @Test
    void shouldImplementValueObjectContract() {
        CustomerId customerId = CustomerId.generate();

        assertThat(customerId)
                .isInstanceOf(ValueObject.class);
    }

    @Test
    void shouldBeSerializable() {
        CustomerId customerId = CustomerId.generate();

        assertThat(customerId)
                .isInstanceOf(Serializable.class);
    }
}
