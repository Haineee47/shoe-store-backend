package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValueObjectArchitectureTest {

    @Test
    void valueObjectShouldBeAnInterface() {
        assertThat(ValueObject.class.isInterface()).isTrue();
    }

    @Test
    void valueObjectShouldNotDeclareState() {
        assertThat(ValueObject.class.getDeclaredFields())
                .isEmpty();
    }

    @Test
    void valueObjectShouldNotDeclareBehaviorContract() {
        assertThat(ValueObject.class.getDeclaredMethods())
                .isEmpty();
    }
}
