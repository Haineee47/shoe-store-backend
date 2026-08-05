package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;

/**
 * Business name of a product.
 *
 * <p>A product name must contain meaningful non-blank text.
 * Leading and trailing whitespace is removed while letter casing
 * is preserved.</p>
 */
public final class ProductName implements ValueObject {

    private final String value;

    private ProductName(String value) {
        this.value = normalize(value);
    }

    public static ProductName of(String value) {
        return new ProductName(value);
    }

    public String value() {
        return value;
    }

    private static String normalize(String value) {
        Objects.requireNonNull(
                value,
                "Product name must not be null"
        );

        String normalizedValue = value.strip();

        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name must not be blank"
            );
        }

        return normalizedValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ProductName that)) {
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
        return value;
    }
}
