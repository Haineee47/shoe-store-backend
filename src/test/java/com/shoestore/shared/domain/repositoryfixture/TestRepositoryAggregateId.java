package com.shoestore.shared.domain.repositoryfixture;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Objects;
import java.util.UUID;

record TestRepositoryAggregateId(UUID value)
        implements ValueObject {

    TestRepositoryAggregateId {
        Objects.requireNonNull(
                value,
                "Aggregate id must not be null"
        );
    }

    static TestRepositoryAggregateId newId() {
        return new TestRepositoryAggregateId(
                UUID.randomUUID()
        );
    }
}
