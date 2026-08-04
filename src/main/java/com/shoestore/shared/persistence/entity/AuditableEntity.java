package com.shoestore.shared.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Base persistence entity for entities that require creation and modification
 * timestamps.
 *
 * <p>Auditing timestamps are assigned by Spring Data JPA through
 * {@link AuditingEntityListener}. The current time is supplied by the
 * application's configured auditing DateTimeProvider.</p>
 *
 * <p>This class intentionally contains no user auditing, soft-delete,
 * ownership, tenant, or business-specific fields.</p>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity extends BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuditableEntity() {
        super();
    }

    /**
     * Returns the instant at which this entity was first persisted.
     *
     * @return creation timestamp, or {@code null} before persistence
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the instant at which this entity was most recently persisted
     * after creation or modification.
     *
     * @return last-modified timestamp, or {@code null} before persistence
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
