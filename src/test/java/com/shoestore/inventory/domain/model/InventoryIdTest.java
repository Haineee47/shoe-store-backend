package com.shoestore.inventory.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class InventoryIdTest {

    @Test
    void shouldGenerateInventoryId() {
        InventoryId inventoryId = InventoryId.generate();

        assertThat(inventoryId).isNotNull();
        assertThat(inventoryId.value()).isNotNull();
    }

    @Test
    void shouldCreateInventoryIdFromUuid() {
        UUID value = UUID.randomUUID();

        InventoryId inventoryId = InventoryId.from(value);

        assertThat(inventoryId.value()).isEqualTo(value);
    }

    @Test
    void shouldRejectNullUuid() {
        assertThatNullPointerException()
                .isThrownBy(() -> InventoryId.from(null))
                .withMessage("Inventory id value must not be null");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        UUID value = UUID.randomUUID();

        InventoryId first = InventoryId.from(value);
        InventoryId second = InventoryId.from(value);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        InventoryId first = InventoryId.generate();
        InventoryId second = InventoryId.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExposeUuidAsStringRepresentation() {
        UUID value = UUID.randomUUID();

        InventoryId inventoryId = InventoryId.from(value);

        assertThat(inventoryId.toString())
                .isEqualTo(value.toString());
    }

    @Test
    void shouldImplementValueObjectContract() {
        InventoryId inventoryId = InventoryId.generate();

        assertThat(inventoryId)
                .isInstanceOf(ValueObject.class);
    }

    @Test
    void shouldBeSerializable() {
        InventoryId inventoryId = InventoryId.generate();

        assertThat(inventoryId)
                .isInstanceOf(Serializable.class);
    }
}
