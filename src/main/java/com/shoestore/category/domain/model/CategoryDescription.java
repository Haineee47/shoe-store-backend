package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;

/**
 * Optional descriptive text of a category.
 *
 * <p>The absence of a description is represented by an empty value object
 * instead of {@code null}.</p>
 */
public final class CategoryDescription implements ValueObject {

    private static final CategoryDescription EMPTY =
            new CategoryDescription("");

    private final String value;

    private CategoryDescription(String value) {
        this.value = normalize(value);
    }

    public static CategoryDescription of(String value) {
        return new CategoryDescription(value);
    }

    public static CategoryDescription empty() {
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
                "Category description must not be null"
        );

        return value.strip();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CategoryDescription that)) {
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
