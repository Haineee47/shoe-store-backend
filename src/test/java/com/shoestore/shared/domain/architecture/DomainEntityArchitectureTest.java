package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.model.DomainEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEntityArchitectureTest {

    @Test
    void domainEntityShouldBeAbstract() {
        assertThat(
                Modifier.isAbstract(
                        DomainEntity.class.getModifiers()
                )
        ).isTrue();
    }

    @Test
    void identityFieldShouldBePrivateAndFinal()
            throws NoSuchFieldException {

        Field id = DomainEntity.class.getDeclaredField("id");

        assertThat(Modifier.isPrivate(id.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(id.getModifiers())).isTrue();
    }

    @Test
    void identityAccessorShouldBeFinal()
            throws NoSuchMethodException {

        Method idMethod = DomainEntity.class.getDeclaredMethod("id");

        assertThat(
                Modifier.isFinal(idMethod.getModifiers())
        ).isTrue();
    }

    @Test
    void equalsShouldBeFinal()
            throws NoSuchMethodException {

        Method equalsMethod =
                DomainEntity.class.getDeclaredMethod(
                        "equals",
                        Object.class
                );

        assertThat(
                Modifier.isFinal(equalsMethod.getModifiers())
        ).isTrue();
    }

    @Test
    void hashCodeShouldBeFinal()
            throws NoSuchMethodException {

        Method hashCodeMethod =
                DomainEntity.class.getDeclaredMethod("hashCode");

        assertThat(
                Modifier.isFinal(hashCodeMethod.getModifiers())
        ).isTrue();
    }

    @Test
    void domainEntityShouldNotHaveNoArgsConstructor() {
        assertThat(DomainEntity.class.getDeclaredConstructors())
                .allSatisfy(constructor ->
                        assertThat(
                                constructor.getParameterCount()
                        ).isGreaterThan(0)
                );
    }
}
