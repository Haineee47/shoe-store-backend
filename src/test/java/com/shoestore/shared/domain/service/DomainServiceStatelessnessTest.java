package com.shoestore.shared.domain.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainServiceStatelessnessTest {

    @Test
    void statelessDomainServiceShouldNotDeclareInstanceState() {
        Field[] instanceFields =
                java.util.Arrays.stream(
                                TestPricingService.class
                                        .getDeclaredFields()
                        )
                        .filter(field ->
                                !Modifier.isStatic(
                                        field.getModifiers()
                                )
                        )
                        .toArray(Field[]::new);

        assertThat(instanceFields).isEmpty();
    }

    @Test
    void concreteDomainServiceShouldBeFinal() {
        assertThat(
                Modifier.isFinal(
                        TestPricingService.class.getModifiers()
                )
        ).isTrue();
    }
}
