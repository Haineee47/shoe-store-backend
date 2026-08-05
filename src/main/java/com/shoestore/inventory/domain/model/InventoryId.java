package com.shoestore.inventory.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identity of an inventory aggregate.
 *
 * <p>This value object prevents inventory identifiers from being confused
 * with identifiers belonging to other business concepts.</p>
 */
public final class InventoryId
        implements ValueObject, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID value;

    private InventoryId(UUID value) {
        this.value = Objects.requireNonNull(
                value,
                "Inventory id value must not be null"
        );
    }

    public static InventoryId generate() {
        return new InventoryId(UUID.randomUUID());
    }

    public static InventoryId from(UUID value) {
        return new InventoryId(value);
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof InventoryId that)) {
            return false;
        }

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
