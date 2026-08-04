package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventArchitectureTest {

    @Test
    void domainEventShouldBeAnInterface() {
        assertThat(DomainEvent.class.isInterface()).isTrue();
    }

    @Test
    void domainEventShouldNotDeclareState() {
        assertThat(DomainEvent.class.getDeclaredFields())
                .isEmpty();
    }

    @Test
    void domainEventShouldDeclareOnlyOccurrenceTimeContract() {
        assertThat(DomainEvent.class.getDeclaredMethods())
                .extracting(Method::getName)
                .containsExactly("occurredAt");
    }

    @Test
    void occurrenceTimeShouldUseInstant() throws Exception {
        Method method =
                DomainEvent.class.getDeclaredMethod("occurredAt");

        assertThat(method.getReturnType())
                .isEqualTo(Instant.class);

        assertThat(method.getParameterCount())
                .isZero();
    }
}
