package com.shoestore.shared.domain.repositoryfixture;

import java.util.Optional;

interface TestAggregateRepository {

    Optional<TestRepositoryAggregate> findById(
            TestRepositoryAggregateId id
    );

    boolean existsById(
            TestRepositoryAggregateId id
    );

    void save(TestRepositoryAggregate aggregate);
}
