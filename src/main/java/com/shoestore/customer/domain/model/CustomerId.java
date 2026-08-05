package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identity of a customer.
 *
 * <p>This value object prevents customer identifiers from being confused
 * with identifiers belonging to other business concepts.</p>
 */
public final class CustomerId
        implements ValueObject, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID value;

    private CustomerId(UUID value) {
        this.value = Objects.requireNonNull(
                value,
                "Customer id value must not be null"
        );
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId from(UUID value) {
        return new CustomerId(value);
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CustomerId that)) {
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
