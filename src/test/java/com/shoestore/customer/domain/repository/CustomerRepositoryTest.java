package com.shoestore.customer.domain.repository;

import com.shoestore.customer.domain.model.Customer;
import com.shoestore.customer.domain.model.CustomerId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryTest {

    @Test
    void shouldBeAnInterface() {
        assertThat(CustomerRepository.class.isInterface()).isTrue();
    }

    @Test
    void shouldDeclareSaveOperationForCustomerAggregate()
            throws NoSuchMethodException {

        Method method = CustomerRepository.class.getDeclaredMethod(
                "save",
                Customer.class
        );

        assertThat(method.getReturnType()).isEqualTo(void.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldDeclareFindByIdUsingStrongCustomerIdentity()
            throws NoSuchMethodException {

        Method method = CustomerRepository.class.getDeclaredMethod(
                "findById",
                CustomerId.class
        );

        assertThat(method.getReturnType()).isEqualTo(Optional.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldReturnOptionalOfCustomerFromFindById()
            throws NoSuchMethodException {

        Method method = CustomerRepository.class.getDeclaredMethod(
                "findById",
                CustomerId.class
        );

        Type genericReturnType = method.getGenericReturnType();

        assertThat(genericReturnType)
                .isInstanceOf(ParameterizedType.class);

        ParameterizedType parameterizedType =
                (ParameterizedType) genericReturnType;

        assertThat(parameterizedType.getRawType())
                .isEqualTo(Optional.class);

        assertThat(parameterizedType.getActualTypeArguments())
                .containsExactly(Customer.class);
    }

    @Test
    void shouldExposeOnlyMinimalAggregateOperations() {
        Method[] declaredMethods =
                CustomerRepository.class.getDeclaredMethods();

        assertThat(declaredMethods)
                .extracting(Method::getName)
                .containsExactlyInAnyOrder(
                        "save",
                        "findById"
                );
    }

    @Test
    void shouldNotExtendAnotherRepositoryAbstraction() {
        assertThat(CustomerRepository.class.getInterfaces())
                .isEmpty();
    }
}
