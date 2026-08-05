package com.shoestore.product.domain.repository;

import com.shoestore.product.domain.model.Product;
import com.shoestore.product.domain.model.ProductId;

import java.util.Optional;

/**
 * Domain repository boundary for the Product aggregate.
 *
 * <p>The repository exposes only operations required to persist and retrieve
 * complete Product aggregates. Implementations belong outside the domain
 * layer and must not leak persistence concerns into this interface.</p>
 */
public interface ProductRepository {

    /**
     * Persists the complete state of a Product aggregate.
     *
     * @param product product aggregate to persist
     */
    void save(Product product);

    /**
     * Finds a Product aggregate by its strongly typed identity.
     *
     * @param id product identity
     * @return matching aggregate, or empty when no product exists
     */
    Optional<Product> findById(ProductId id);
}
