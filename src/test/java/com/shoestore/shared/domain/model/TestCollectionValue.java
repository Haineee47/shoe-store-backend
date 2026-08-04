package com.shoestore.shared.domain.model;

import java.util.List;

record TestCollectionValue(List<String> values)
        implements ValueObject {

    TestCollectionValue {
        if (values == null) {
            throw new IllegalArgumentException(
                    "Values must not be null"
            );
        }

        if (values.stream().anyMatch(value -> value == null)) {
            throw new IllegalArgumentException(
                    "Values must not contain null"
            );
        }

        values = List.copyOf(values);
    }
}
