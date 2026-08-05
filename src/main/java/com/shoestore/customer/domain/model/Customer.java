package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.AggregateRoot;

import java.util.Objects;

/**
 * Aggregate root representing a customer profile.
 *
 * <p>The Customer aggregate owns the customer's business-facing name,
 * contact email address, and profile lifecycle status.</p>
 *
 * <p>This aggregate does not represent an authentication account. Passwords,
 * login credentials, roles, permissions, email verification, security locks,
 * carts, orders, addresses, and payment details remain outside its scope.</p>
 */
public final class Customer extends AggregateRoot<CustomerId> {

    private CustomerName name;
    private EmailAddress email;
    private CustomerStatus status;

    private Customer(
            CustomerId id,
            CustomerName name,
            EmailAddress email
    ) {
        super(id);
        this.name = requireName(name);
        this.email = requireEmail(email);
        this.status = CustomerStatus.ACTIVE;
    }

    /**
     * Creates a new active customer profile with an explicit identity.
     */
    public static Customer create(
            CustomerId id,
            CustomerName name,
            EmailAddress email
    ) {
        return new Customer(id, name, email);
    }

    public CustomerName name() {
        return name;
    }

    public EmailAddress email() {
        return email;
    }

    public CustomerStatus status() {
        return status;
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }

    public boolean isInactive() {
        return status == CustomerStatus.INACTIVE;
    }

    /**
     * Changes the customer's business-facing name.
     */
    public void rename(CustomerName newName) {
        this.name = requireName(newName);
    }

    /**
     * Changes the customer's contact email address.
     *
     * <p>Email ownership, uniqueness, and verification are not checked by
     * this aggregate because they require external policies or workflows.</p>
     */
    public void changeEmail(EmailAddress newEmail) {
        this.email = requireEmail(newEmail);
    }

    /**
     * Enables the customer profile for business use.
     */
    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }

    /**
     * Disables the customer profile from business use.
     */
    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }

    private static CustomerName requireName(
            CustomerName name
    ) {
        return Objects.requireNonNull(
                name,
                "Customer name must not be null"
        );
    }

    private static EmailAddress requireEmail(
            EmailAddress email
    ) {
        return Objects.requireNonNull(
                email,
                "Customer email address must not be null"
        );
    }
}
