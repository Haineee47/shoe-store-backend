package com.shoestore.shared.domain.model;

import java.util.Objects;

/**
 * Base type for domain entities whose identity remains stable throughout
 * their lifecycle.
 *
 * <p>This type belongs exclusively to the domain layer. It must not contain
 * persistence annotations, framework dependencies, persistence state,
 * auditing fields, or optimistic-locking concerns.</p>
 *
 * @param <ID> immutable identity type of the entity
 */
public abstract class DomainEntity<ID> {

    private final ID id;

    protected DomainEntity(ID id) {
        this.id = Objects.requireNonNull(
                id,
                "Domain entity id must not be null"
        );
    }

    public final ID id() {
        return id;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        DomainEntity<?> that = (DomainEntity<?>) other;
        return id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
