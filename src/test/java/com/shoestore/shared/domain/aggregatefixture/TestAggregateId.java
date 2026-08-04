package com.shoestore.shared.domain.aggregatefixture;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;
import java.util.UUID;

record TestAggregateId(UUID value) implements ValueObject {

    TestAggregateId {
        Objects.requireNonNull(
                value,
                "Test aggregate id must not be null"
        );
    }

    static TestAggregateId newId() {
        return new TestAggregateId(UUID.randomUUID());
    }
}
