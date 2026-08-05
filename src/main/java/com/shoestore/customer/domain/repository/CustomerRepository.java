package com.shoestore.customer.domain.repository;

import com.shoestore.customer.domain.model.Customer;
import com.shoestore.customer.domain.model.CustomerId;

import java.util.Optional;

/**
 * Domain repository boundary for the Customer aggregate.
 *
 * <p>The repository exposes only operations required to persist and retrieve
 * complete Customer aggregates. Implementations belong outside the domain
 * layer and must not leak persistence or framework concerns into this
 * interface.</p>
 */
public interface CustomerRepository {

    /**
     * Persists the complete state of a Customer aggregate.
     *
     * @param customer customer aggregate to persist
     */
    void save(Customer customer);

    /**
     * Finds a Customer aggregate by its strongly typed identity.
     *
     * @param id customer identity
     * @return matching aggregate, or empty when no customer exists
     */
    Optional<Customer> findById(CustomerId id);
}
