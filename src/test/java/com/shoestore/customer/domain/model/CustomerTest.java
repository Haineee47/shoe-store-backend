package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.AggregateRoot;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CustomerTest {

    @Test
    void shouldCreateActiveCustomer() {
        CustomerId id = CustomerId.generate();
        CustomerName name =
                CustomerName.of("Phan Tấn Hải");
        EmailAddress email =
                EmailAddress.of("hai@example.com");

        Customer customer = Customer.create(
                id,
                name,
                email
        );

        assertThat(customer.id()).isEqualTo(id);
        assertThat(customer.name()).isEqualTo(name);
        assertThat(customer.email()).isEqualTo(email);
        assertThat(customer.status())
                .isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.isActive()).isTrue();
        assertThat(customer.isInactive()).isFalse();
    }

    @Test
    void shouldRejectNullIdentityWhenCreatingCustomer() {
        assertThatNullPointerException()
                .isThrownBy(() -> Customer.create(
                        null,
                        CustomerName.of("Phan Tấn Hải"),
                        EmailAddress.of("hai@example.com")
                ))
                .withMessage(
                        "Domain entity id must not be null"
                );
    }

    @Test
    void shouldRejectNullNameWhenCreatingCustomer() {
        assertThatNullPointerException()
                .isThrownBy(() -> Customer.create(
                        CustomerId.generate(),
                        null,
                        EmailAddress.of("hai@example.com")
                ))
                .withMessage(
                        "Customer name must not be null"
                );
    }

    @Test
    void shouldRejectNullEmailWhenCreatingCustomer() {
        assertThatNullPointerException()
                .isThrownBy(() -> Customer.create(
                        CustomerId.generate(),
                        CustomerName.of("Phan Tấn Hải"),
                        null
                ))
                .withMessage(
                        "Customer email address must not be null"
                );
    }

    @Test
    void shouldRenameCustomer() {
        Customer customer = createCustomer();

        CustomerName newName =
                CustomerName.of("Phan Hải");

        customer.rename(newName);

        assertThat(customer.name()).isEqualTo(newName);
    }

    @Test
    void shouldRejectNullNameWhenRenamingCustomer() {
        Customer customer = createCustomer();

        assertThatNullPointerException()
                .isThrownBy(() -> customer.rename(null))
                .withMessage(
                        "Customer name must not be null"
                );
    }

    @Test
    void shouldPreserveNameWhenRenameFails() {
        Customer customer = createCustomer();
        CustomerName originalName = customer.name();

        assertThatNullPointerException()
                .isThrownBy(() -> customer.rename(null));

        assertThat(customer.name())
                .isEqualTo(originalName);
    }

    @Test
    void shouldChangeCustomerEmail() {
        Customer customer = createCustomer();

        EmailAddress newEmail =
                EmailAddress.of("new-address@example.com");

        customer.changeEmail(newEmail);

        assertThat(customer.email()).isEqualTo(newEmail);
    }

    @Test
    void shouldRejectNullEmailWhenChangingEmail() {
        Customer customer = createCustomer();

        assertThatNullPointerException()
                .isThrownBy(
                        () -> customer.changeEmail(null)
                )
                .withMessage(
                        "Customer email address must not be null"
                );
    }

    @Test
    void shouldPreserveEmailWhenChangeFails() {
        Customer customer = createCustomer();
        EmailAddress originalEmail = customer.email();

        assertThatNullPointerException()
                .isThrownBy(
                        () -> customer.changeEmail(null)
                );

        assertThat(customer.email())
                .isEqualTo(originalEmail);
    }

    @Test
    void shouldDeactivateActiveCustomer() {
        Customer customer = createCustomer();

        customer.deactivate();

        assertThat(customer.status())
                .isEqualTo(CustomerStatus.INACTIVE);
        assertThat(customer.isInactive()).isTrue();
        assertThat(customer.isActive()).isFalse();
    }

    @Test
    void shouldDeactivateIdempotently() {
        Customer customer = createCustomer();

        customer.deactivate();
        customer.deactivate();

        assertThat(customer.status())
                .isEqualTo(CustomerStatus.INACTIVE);
    }

    @Test
    void shouldActivateInactiveCustomer() {
        Customer customer = createCustomer();
        customer.deactivate();

        customer.activate();

        assertThat(customer.status())
                .isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.isActive()).isTrue();
        assertThat(customer.isInactive()).isFalse();
    }

    @Test
    void shouldActivateIdempotently() {
        Customer customer = createCustomer();

        customer.activate();
        customer.activate();

        assertThat(customer.status())
                .isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    void shouldAllowRenamingInactiveCustomer() {
        Customer customer = createCustomer();
        customer.deactivate();

        CustomerName newName =
                CustomerName.of("Phan Hải");

        customer.rename(newName);

        assertThat(customer.name()).isEqualTo(newName);
        assertThat(customer.isInactive()).isTrue();
    }

    @Test
    void shouldAllowChangingEmailForInactiveCustomer() {
        Customer customer = createCustomer();
        customer.deactivate();

        EmailAddress newEmail =
                EmailAddress.of("inactive@example.com");

        customer.changeEmail(newEmail);

        assertThat(customer.email()).isEqualTo(newEmail);
        assertThat(customer.isInactive()).isTrue();
    }

    @Test
    void shouldBeEqualWhenCustomerIdsAreEqual() {
        CustomerId id = CustomerId.generate();

        Customer first = Customer.create(
                id,
                CustomerName.of("Phan Tấn Hải"),
                EmailAddress.of("hai@example.com")
        );

        Customer second = Customer.create(
                id,
                CustomerName.of("Nguyễn Văn An"),
                EmailAddress.of("an@example.com")
        );

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenCustomerIdsAreDifferent() {
        Customer first = createCustomer();
        Customer second = createCustomer();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExtendAggregateRoot() {
        Customer customer = createCustomer();

        assertThat(customer)
                .isInstanceOf(AggregateRoot.class);
    }

    private static Customer createCustomer() {
        return Customer.create(
                CustomerId.generate(),
                CustomerName.of("Phan Tấn Hải"),
                EmailAddress.of("hai@example.com")
        );
    }
}
