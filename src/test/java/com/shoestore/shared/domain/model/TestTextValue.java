package com.shoestore.shared.domain.model;

record TestTextValue(String value) implements ValueObject {

    TestTextValue {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Test text value must not be null"
            );
        }

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "Test text value must not be blank"
            );
        }

        value = value.trim();
    }
}
