package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;

/**
 * Name of a product category.
 *
 * <p>A category name must contain meaningful non-blank text. Leading and
 * trailing whitespace is removed when the value object is created.</p>
 */
public final class CategoryName implements ValueObject {

    private final String value;

    private CategoryName(String value) {
        this.value = normalize(value);
    }

    public static CategoryName of(String value) {
        return new CategoryName(value);
    }

    public String value() {
        return value;
    }

    private static String normalize(String value) {
        Objects.requireNonNull(
                value,
                "Category name must not be null"
        );

        String normalizedValue = value.strip();

        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException(
                    "Category name must not be blank"
            );
        }

        return normalizedValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CategoryName that)) {
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
