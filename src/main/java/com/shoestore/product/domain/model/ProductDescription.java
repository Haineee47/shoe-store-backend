package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;

/**
 * Optional descriptive text of a product.
 *
 * <p>The absence of a product description is represented by an empty
 * value object rather than {@code null}.</p>
 */
public final class ProductDescription implements ValueObject {

    private static final ProductDescription EMPTY =
            new ProductDescription("");

    private final String value;

    private ProductDescription(String value) {
        this.value = normalize(value);
    }

    public static ProductDescription of(String value) {
        return new ProductDescription(value);
    }

    public static ProductDescription empty() {
        return EMPTY;
    }

    public String value() {
        return value;
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    private static String normalize(String value) {
        Objects.requireNonNull(
                value,
                "Product description must not be null"
        );

        return value.strip();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ProductDescription that)) {
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
