package com.shoestore.shared.domain.aggregatefixture;

import com.shoestore.shared.domain.model.AggregateRoot;
import com.shoestore.shared.domain.model.DomainEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AggregateRootTest {

    @Test
    void aggregateRootShouldAlsoBeADomainEntity() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        assertThat(aggregate)
                .isInstanceOf(AggregateRoot.class)
                .isInstanceOf(DomainEntity.class);
    }

    @Test
    void shouldExposeAggregateIdentity() {
        TestAggregateId id =
                TestAggregateId.newId();

        TestAggregateRoot aggregate =
                new TestAggregateRoot(id);

        assertThat(aggregate.id()).isEqualTo(id);
    }

    @Test
    void shouldAddChildThroughAggregateRoot() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        TestChildId childId = TestChildId.newId();

        aggregate.addItem(childId, 2);

        assertThat(aggregate.items())
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.id()).isEqualTo(childId);
                    assertThat(item.quantity()).isEqualTo(2);
                });
    }

    @Test
    void shouldUpdateExistingChildThroughAggregateRoot() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        TestChildId childId = TestChildId.newId();

        aggregate.addItem(childId, 2);
        aggregate.addItem(childId, 3);

        assertThat(aggregate.items())
                .singleElement()
                .satisfies(item ->
                        assertThat(item.quantity()).isEqualTo(5)
                );
    }

    @Test
    void shouldRemoveChildThroughAggregateRoot() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        TestChildId childId = TestChildId.newId();

        aggregate.addItem(childId, 2);
        aggregate.removeItem(childId);

        assertThat(aggregate.items()).isEmpty();
    }

    @Test
    void shouldRejectRemovingUnknownChild() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        aggregate.removeItem(
                                TestChildId.newId()
                        )
                )
                .withMessage(
                        "Item does not belong to aggregate"
                );
    }

    @Test
    void shouldProtectAggregateInvariant() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        aggregate.addItem(TestChildId.newId(), 80);

        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        aggregate.addItem(
                                TestChildId.newId(),
                                21
                        )
                )
                .withMessage(
                        "Aggregate total quantity must not exceed 100"
                );

        assertThat(aggregate.totalQuantity())
                .isEqualTo(80);
    }

    @Test
    void failedOperationShouldNotPartiallyMutateAggregate() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        TestChildId existingId = TestChildId.newId();

        aggregate.addItem(existingId, 90);

        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        aggregate.addItem(existingId, 11)
                );

        assertThat(aggregate.items())
                .singleElement()
                .satisfies(item ->
                        assertThat(item.quantity()).isEqualTo(90)
                );
    }

    @Test
    void shouldNotExposeMutableChildCollection() {
        TestAggregateRoot aggregate =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        aggregate.addItem(TestChildId.newId(), 1);

        List<TestAggregateItem> exposedItems =
                aggregate.items();

        assertThrows(
                UnsupportedOperationException.class,
                () -> exposedItems.clear()
        );

        assertThat(aggregate.items()).hasSize(1);
    }

    @Test
    void equalityShouldRemainBasedOnRootIdentity() {
        TestAggregateId id =
                TestAggregateId.newId();

        TestAggregateRoot first =
                new TestAggregateRoot(id);

        TestAggregateRoot second =
                new TestAggregateRoot(id);

        first.addItem(TestChildId.newId(), 1);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void rootsWithDifferentIdentitiesShouldNotBeEqual() {
        TestAggregateRoot first =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        TestAggregateRoot second =
                new TestAggregateRoot(
                        TestAggregateId.newId()
                );

        assertThat(first).isNotEqualTo(second);
    }
}
