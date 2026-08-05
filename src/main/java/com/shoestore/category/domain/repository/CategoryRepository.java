package com.shoestore.category.domain.repository;

import com.shoestore.category.domain.model.Category;
import com.shoestore.category.domain.model.CategoryId;

import java.util.Optional;

/**
 * Domain repository boundary for the Category aggregate.
 *
 * <p>The repository exposes only operations required to persist and retrieve
 * complete Category aggregates. Implementations belong outside the domain
 * layer and may use persistence technologies without leaking those concerns
 * into this interface.</p>
 */
public interface CategoryRepository {

    /**
     * Persists the complete state of a Category aggregate.
     *
     * @param category category aggregate to persist
     */
    void save(Category category);

    /**
     * Finds a Category aggregate by its strongly typed identity.
     *
     * @param id category identity
     * @return the matching aggregate, or empty when no category exists
     */
    Optional<Category> findById(CategoryId id);
}
