package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.exception.BusinessRuleViolationException;
import com.shoestore.shared.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionConventionTest {

    @Test
    void domainExceptionShouldBeAbstract() {
        assertThat(
                Modifier.isAbstract(
                        DomainException.class.getModifiers()
                )
        ).isTrue();
    }

    @Test
    void businessRuleViolationExceptionShouldBeAbstract() {
        assertThat(
                Modifier.isAbstract(
                        BusinessRuleViolationException.class.getModifiers()
                )
        ).isTrue();
    }

    @Test
    void businessRuleViolationExceptionShouldExtendDomainException() {
        assertThat(
                BusinessRuleViolationException.class.getSuperclass()
        ).isEqualTo(DomainException.class);
    }

    @Test
    void businessRuleViolationConstructorsShouldNotBePublic() {
        Constructor<?>[] constructors =
                BusinessRuleViolationException.class
                        .getDeclaredConstructors();

        assertThat(constructors)
                .isNotEmpty()
                .allSatisfy(constructor ->
                        assertThat(
                                Modifier.isPublic(
                                        constructor.getModifiers()
                                )
                        ).isFalse()
                );
    }

    @Test
    void businessRuleViolationConstructorsShouldBeProtected() {
        boolean allConstructorsAreProtected =
                Arrays.stream(
                                BusinessRuleViolationException.class
                                        .getDeclaredConstructors()
                        )
                        .allMatch(constructor ->
                                Modifier.isProtected(
                                        constructor.getModifiers()
                                )
                        );

        assertThat(allConstructorsAreProtected).isTrue();
    }
}
