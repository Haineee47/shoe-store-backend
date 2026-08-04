package com.shoestore.shared.domain.repositoryfixture;

import com.shoestore.shared.domain.model.AggregateRoot;

final class TestRepositoryAggregate
        extends AggregateRoot<TestRepositoryAggregateId> {

    private String name;

    TestRepositoryAggregate(
            TestRepositoryAggregateId id,
            String name
    ) {
        super(id);
        this.name = requireName(name);
    }

    String name() {
        return name;
    }

    void rename(String newName) {
        this.name = requireName(newName);
    }

    private static String requireName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Aggregate name must not be null"
            );
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException(
                    "Aggregate name must not be blank"
            );
        }

        return name.trim();
    }
}
