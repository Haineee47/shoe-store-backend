package com.shoestore.inventory.domain.repository;

import com.shoestore.inventory.domain.model.Inventory;
import com.shoestore.inventory.domain.model.InventoryId;

import java.util.Optional;

/**
 * Domain repository boundary for the Inventory aggregate.
 *
 * <p>The repository exposes only operations required to persist and retrieve
 * complete Inventory aggregates. Implementations belong outside the domain
 * layer and must not leak persistence concerns into this interface.</p>
 */
public interface InventoryRepository {

    /**
     * Persists the complete state of an Inventory aggregate.
     *
     * @param inventory inventory aggregate to persist
     */
    void save(Inventory inventory);

    /**
     * Finds an Inventory aggregate by its strongly typed identity.
     *
     * @param id inventory identity
     * @return matching aggregate, or empty when no inventory exists
     */
    Optional<Inventory> findById(InventoryId id);
}
