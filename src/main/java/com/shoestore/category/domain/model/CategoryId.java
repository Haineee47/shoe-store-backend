package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identity of a category.
 *
 * <p>This value object prevents category identifiers from being confused
 * with identifiers belonging to other business concepts.</p>
 */
public final class CategoryId
        implements ValueObject, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID value;

    private CategoryId(UUID value) {
        this.value = Objects.requireNonNull(
                value,
                "Category id value must not be null"
        );
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    public static CategoryId from(UUID value) {
        return new CategoryId(value);
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CategoryId that)) {
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
