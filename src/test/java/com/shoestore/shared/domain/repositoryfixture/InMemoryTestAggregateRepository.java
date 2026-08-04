package com.shoestore.shared.domain.repositoryfixture;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

final class InMemoryTestAggregateRepository
        implements TestAggregateRepository {

    private final Map<
            TestRepositoryAggregateId,
            TestRepositoryAggregate
            > storage = new HashMap<>();

    @Override
    public Optional<TestRepositoryAggregate> findById(
            TestRepositoryAggregateId id
    ) {
        Objects.requireNonNull(
                id,
                "Aggregate id must not be null"
        );

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(
            TestRepositoryAggregateId id
    ) {
        Objects.requireNonNull(
                id,
                "Aggregate id must not be null"
        );

        return storage.containsKey(id);
    }

    @Override
    public void save(TestRepositoryAggregate aggregate) {
        Objects.requireNonNull(
                aggregate,
                "Aggregate must not be null"
        );

        storage.put(aggregate.id(), aggregate);
    }
}
