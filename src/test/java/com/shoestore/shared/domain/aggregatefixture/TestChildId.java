package com.shoestore.shared.domain.aggregatefixture;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;
import java.util.UUID;

record TestChildId(UUID value) implements ValueObject {

    TestChildId {
        Objects.requireNonNull(
                value,
                "Test child id must not be null"
        );
    }

    static TestChildId newId() {
        return new TestChildId(UUID.randomUUID());
    }
}
