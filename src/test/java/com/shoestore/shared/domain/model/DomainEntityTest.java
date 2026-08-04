package com.shoestore.shared.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class DomainEntityTest {

    @Test
    void shouldExposeIdentity() {
        UUID id = UUID.randomUUID();

        TestDomainEntity entity =
                new TestDomainEntity(id, "Original name");

        assertThat(entity.id()).isEqualTo(id);
    }

    @Test
    void shouldRejectNullIdentity() {
        assertThatNullPointerException()
                .isThrownBy(
                        () -> new TestDomainEntity(
                                null,
                                "Original name"
                        )
                )
                .withMessage(
                        "Domain entity id must not be null"
                );
    }

    @Test
    void shouldBeEqualToItself() {
        TestDomainEntity entity =
                new TestDomainEntity(
                        UUID.randomUUID(),
                        "Original name"
                );

        assertThat(entity).isEqualTo(entity);
    }

    @Test
    void shouldBeEqualWhenConcreteTypeAndIdentityAreEqual() {
        UUID id = UUID.randomUUID();

        TestDomainEntity first =
                new TestDomainEntity(id, "First name");

        TestDomainEntity second =
                new TestDomainEntity(id, "Second name");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenIdentitiesDiffer() {
        TestDomainEntity first =
                new TestDomainEntity(
                        UUID.randomUUID(),
                        "Same name"
                );

        TestDomainEntity second =
                new TestDomainEntity(
                        UUID.randomUUID(),
                        "Same name"
                );

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldNotBeEqualToNull() {
        TestDomainEntity entity =
                new TestDomainEntity(
                        UUID.randomUUID(),
                        "Original name"
                );

        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToUnrelatedObject() {
        UUID id = UUID.randomUUID();

        TestDomainEntity entity =
                new TestDomainEntity(id, "Original name");

        assertThat(entity).isNotEqualTo(id);
    }

    @Test
    void shouldNotBeEqualWhenConcreteTypesDiffer() {
        UUID id = UUID.randomUUID();

        TestDomainEntity first =
                new TestDomainEntity(id, "Original name");

        AnotherTestDomainEntity second =
                new AnotherTestDomainEntity(id);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldKeepEqualityWhenBusinessStateChanges() {
        UUID id = UUID.randomUUID();

        TestDomainEntity first =
                new TestDomainEntity(id, "Original name");

        TestDomainEntity second =
                new TestDomainEntity(id, "Original name");

        first.rename("Changed name");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldBehaveConsistentlyInHashSet() {
        UUID id = UUID.randomUUID();

        TestDomainEntity first =
                new TestDomainEntity(id, "First name");

        TestDomainEntity duplicate =
                new TestDomainEntity(id, "Second name");

        HashSet<TestDomainEntity> entities = new HashSet<>();
        entities.add(first);
        entities.add(duplicate);

        assertThat(entities)
                .hasSize(1)
                .contains(first);
    }
}
