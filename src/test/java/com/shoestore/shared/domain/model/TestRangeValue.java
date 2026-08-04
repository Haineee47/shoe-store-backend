package com.shoestore.shared.domain.model;

import java.util.Objects;

final class TestRangeValue implements ValueObject {

    private final int minimum;
    private final int maximum;

    TestRangeValue(int minimum, int maximum) {
        if (minimum > maximum) {
            throw new IllegalArgumentException(
                    "Minimum must not be greater than maximum"
            );
        }

        this.minimum = minimum;
        this.maximum = maximum;
    }

    int minimum() {
        return minimum;
    }

    int maximum() {
        return maximum;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        TestRangeValue that = (TestRangeValue) other;

        return minimum == that.minimum
                && maximum == that.maximum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum);
    }
}
