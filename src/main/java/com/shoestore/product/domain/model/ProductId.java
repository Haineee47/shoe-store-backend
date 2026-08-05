package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identity of a product.
 *
 * <p>This value object prevents product identifiers from being confused
 * with identifiers belonging to other business concepts.</p>
 */
public final class ProductId
        implements ValueObject, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID value;

    private ProductId(UUID value) {
        this.value = Objects.requireNonNull(
                value,
                "Product id value must not be null"
        );
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId from(UUID value) {
        return new ProductId(value);
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ProductId that)) {
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
