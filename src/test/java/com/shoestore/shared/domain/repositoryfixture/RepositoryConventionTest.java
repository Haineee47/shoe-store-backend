package com.shoestore.shared.domain.repositoryfixture;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryConventionTest {

    @Test
    void repositoryContractShouldBeAnInterface() {
        assertThat(
                TestAggregateRepository.class.isInterface()
        ).isTrue();
    }

    @Test
    void repositoryContractShouldNotDeclareState() {
        assertThat(
                TestAggregateRepository.class.getDeclaredFields()
        ).isEmpty();
    }

    @Test
    void repositoryContractShouldNotExtendAnotherRepository() {
        assertThat(
                TestAggregateRepository.class.getInterfaces()
        ).isEmpty();
    }

    @Test
    void repositoryMethodsShouldRemainAbstract() {
        assertThat(
                TestAggregateRepository.class.getDeclaredMethods()
        )
                .isNotEmpty()
                .allSatisfy(method ->
                        assertThat(
                                Modifier.isAbstract(
                                        method.getModifiers()
                                )
                        ).isTrue()
                );
    }

    @Test
    void saveShouldAcceptConcreteAggregateAndReturnVoid()
            throws NoSuchMethodException {

        var method =
                TestAggregateRepository.class.getDeclaredMethod(
                        "save",
                        TestRepositoryAggregate.class
                );

        assertThat(method.getReturnType())
                .isEqualTo(void.class);

        assertThat(method.getTypeParameters())
                .isEmpty();
    }
}
