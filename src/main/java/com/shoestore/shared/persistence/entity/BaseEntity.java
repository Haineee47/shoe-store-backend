package com.shoestore.shared.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

/**
 * Base persistence entity for entities using the standard project identity
 * and optimistic-locking conventions.
 *
 * <p>The identifier is assigned by the application when the Java object is
 * created. It must therefore remain stable for the entire lifetime of the
 * entity instance.</p>
 *
 * <p>This class intentionally contains no auditing, soft-delete, tenant,
 * ownership, or business-specific state.</p>
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
    }

    /**
     * Returns the stable identifier assigned when this entity instance was
     * created.
     *
     * @return non-null entity identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the optimistic-lock version managed by the persistence provider.
     *
     * <p>Application code may read this value but must not modify it.</p>
     *
     * @return the version, or {@code null} before the entity is first persisted
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Indicates whether this entity has received a persistence version.
     *
     * <p>This method must not use the identifier because application-assigned
     * identifiers are available before persistence.</p>
     *
     * @return {@code true} when a persistence version has been assigned
     */
    public boolean isPersisted() {
        return version != null;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (Hibernate.getClass(this) != Hibernate.getClass(other)) {
            return false;
        }

        BaseEntity that = (BaseEntity) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(Hibernate.getClass(this), id);
    }
}
