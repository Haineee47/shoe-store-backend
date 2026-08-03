package com.shoestore.shared.persistence.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    @Test
    void shouldAssignIdentifierWhenEntityIsCreated() {
        TestEntity entity = new TestEntity();

        assertThat(entity.getId()).isNotNull();
    }

    @Test
    void shouldAssignDifferentIdentifiersToDifferentEntities() {
        TestEntity first = new TestEntity();
        TestEntity second = new TestEntity();

        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    void shouldStartWithoutPersistenceVersion() {
        TestEntity entity = new TestEntity();

        assertThat(entity.getVersion()).isNull();
        assertThat(entity.isPersisted()).isFalse();
    }

    @Test
    void shouldReportPersistedWhenVersionHasBeenAssigned() throws Exception {
        TestEntity entity = new TestEntity();

        assignVersion(entity, 0L);

        assertThat(entity.getVersion()).isZero();
        assertThat(entity.isPersisted()).isTrue();
    }

    @Test
    void shouldBeEqualToItself() {
        TestEntity entity = new TestEntity();

        assertThat(entity).isEqualTo(entity);
    }

    @Test
    void shouldNotEqualNull() {
        TestEntity entity = new TestEntity();

        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    void shouldNotEqualEntityWithDifferentIdentifier() {
        TestEntity first = new TestEntity();
        TestEntity second = new TestEntity();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldBeEqualWhenTypeAndIdentifierAreEqual() throws Exception {
        TestEntity first = new TestEntity();
        TestEntity second = new TestEntity();

        assignId(second, first.getId());

        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void shouldNotEqualDifferentEntityTypesEvenWhenIdentifierIsEqual()
            throws Exception {
        TestEntity first = new TestEntity();
        AnotherTestEntity second = new AnotherTestEntity();

        assignId(second, first.getId());

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldRemainFindableInHashSetBeforePersistence() throws Exception {
        TestEntity entity = new TestEntity();
        HashSet<TestEntity> entities = new HashSet<>();

        entities.add(entity);
        assignVersion(entity, 0L);

        assertThat(entities).contains(entity);
    }

    private static void assignId(BaseEntity entity, UUID id)
            throws ReflectiveOperationException {
        Field idField = BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    private static void assignVersion(BaseEntity entity, Long version)
            throws ReflectiveOperationException {
        Field versionField = BaseEntity.class.getDeclaredField("version");
        versionField.setAccessible(true);
        versionField.set(entity, version);
    }

    private static final class TestEntity extends BaseEntity {
    }

    private static final class AnotherTestEntity extends BaseEntity {
    }
}
