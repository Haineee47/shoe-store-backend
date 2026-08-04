package com.shoestore.shared.domain.model;

/**
 * Base type for an entity that defines an aggregate consistency boundary.
 *
 * <p>All state changes inside an aggregate must be coordinated through its
 * root. External code must not directly mutate aggregate internals.</p>
 *
 * @param <ID> immutable identity type of the aggregate root
 */
public abstract class AggregateRoot<ID>
        extends DomainEntity<ID> {

    protected AggregateRoot(ID id) {
        super(id);
    }
}
