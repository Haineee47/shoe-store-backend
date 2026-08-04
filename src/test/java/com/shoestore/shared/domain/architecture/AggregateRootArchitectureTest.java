package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.model.AggregateRoot;
import com.shoestore.shared.domain.model.DomainEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class AggregateRootArchitectureTest {

    @Test
    void aggregateRootShouldBeAbstract() {
        assertThat(
                Modifier.isAbstract(
                        AggregateRoot.class.getModifiers()
                )
        ).isTrue();
    }

    @Test
    void aggregateRootShouldExtendDomainEntity() {
        assertThat(AggregateRoot.class.getSuperclass())
                .isEqualTo(DomainEntity.class);
    }

    @Test
    void aggregateRootShouldDeclareOnlyProtectedConstructors() {
        Constructor<?>[] constructors =
                AggregateRoot.class.getDeclaredConstructors();

        assertThat(constructors)
                .isNotEmpty()
                .allSatisfy(constructor ->
                        assertThat(
                                Modifier.isProtected(
                                        constructor.getModifiers()
                                )
                        ).isTrue()
                );
    }

    @Test
    void aggregateRootShouldNotDeclareAdditionalState() {
        assertThat(AggregateRoot.class.getDeclaredFields())
                .isEmpty();
    }

    @Test
    void aggregateRootShouldNotDeclareGenericOperations() {
        assertThat(AggregateRoot.class.getDeclaredMethods())
                .isEmpty();
    }
}
