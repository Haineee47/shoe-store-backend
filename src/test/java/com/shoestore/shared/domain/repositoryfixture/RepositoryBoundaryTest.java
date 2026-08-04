package com.shoestore.shared.domain.repositoryfixture;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class RepositoryBoundaryTest {

    private final TestAggregateRepository repository =
            new InMemoryTestAggregateRepository();

    @Test
    void shouldReturnEmptyWhenAggregateDoesNotExist() {
        Optional<TestRepositoryAggregate> result =
                repository.findById(
                        TestRepositoryAggregateId.newId()
                );

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveAndFindAggregateByIdentity() {
        TestRepositoryAggregateId id =
                TestRepositoryAggregateId.newId();

        TestRepositoryAggregate aggregate =
                new TestRepositoryAggregate(
                        id,
                        "Original name"
                );

        repository.save(aggregate);

        assertThat(repository.findById(id))
                .containsSame(aggregate);
    }

    @Test
    void shouldReportWhetherAggregateExists() {
        TestRepositoryAggregate aggregate =
                new TestRepositoryAggregate(
                        TestRepositoryAggregateId.newId(),
                        "Original name"
                );

        assertThat(
                repository.existsById(aggregate.id())
        ).isFalse();

        repository.save(aggregate);

        assertThat(
                repository.existsById(aggregate.id())
        ).isTrue();
    }

    @Test
    void savingSameIdentityShouldReplaceStoredState() {
        TestRepositoryAggregateId id =
                TestRepositoryAggregateId.newId();

        TestRepositoryAggregate original =
                new TestRepositoryAggregate(
                        id,
                        "Original name"
                );

        TestRepositoryAggregate updated =
                new TestRepositoryAggregate(
                        id,
                        "Updated name"
                );

        repository.save(original);
        repository.save(updated);

        assertThat(repository.findById(id))
                .containsSame(updated);
    }

    @Test
    void shouldRejectNullIdentityWhenFinding() {
        assertThatNullPointerException()
                .isThrownBy(() -> repository.findById(null))
                .withMessage(
                        "Aggregate id must not be null"
                );
    }

    @Test
    void shouldRejectNullIdentityWhenCheckingExistence() {
        assertThatNullPointerException()
                .isThrownBy(() -> repository.existsById(null))
                .withMessage(
                        "Aggregate id must not be null"
                );
    }

    @Test
    void shouldRejectNullAggregateWhenSaving() {
        assertThatNullPointerException()
                .isThrownBy(() -> repository.save(null))
                .withMessage(
                        "Aggregate must not be null"
                );
    }
}
