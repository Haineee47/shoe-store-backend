package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;

/**
 * Business-facing name of a customer.
 *
 * <p>A customer name must contain meaningful non-blank text.
 * Leading and trailing whitespace is removed while the original
 * letter casing and internal spacing are preserved.</p>
 */
public final class CustomerName implements ValueObject {

    private final String value;

    private CustomerName(String value) {
        this.value = normalize(value);
    }

    public static CustomerName of(String value) {
        return new CustomerName(value);
    }

    public String value() {
        return value;
    }

    private static String normalize(String value) {
        Objects.requireNonNull(
                value,
                "Customer name must not be null"
        );

        String normalizedValue = value.strip();

        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer name must not be blank"
            );
        }

        return normalizedValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CustomerName that)) {
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
