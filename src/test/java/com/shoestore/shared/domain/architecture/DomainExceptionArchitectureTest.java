package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.exception.DomainException;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionArchitectureTest {

    private static final String DOMAIN_PACKAGE =
            "com.shoestore.shared.domain..";

    private final JavaClasses productionClasses =
            new ClassFileImporter()
                    .importPackages("com.shoestore");

    @Test
    void domainShouldNotDependOnSpring() {
        noClasses()
                .that()
                .resideInAPackage(DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..")
                .check(productionClasses);
    }

    @Test
    void domainShouldNotDependOnPersistenceFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "org.springframework.data..",
                        "java.sql.."
                )
                .check(productionClasses);
    }

    @Test
    void domainShouldNotDependOnWebLayer() {
        noClasses()
                .that()
                .resideInAPackage(DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.web..",
                        "org.springframework.web..",
                        "jakarta.servlet.."
                )
                .check(productionClasses);
    }

    @Test
    void domainShouldNotDependOnApplicationLayer() {
        noClasses()
                .that()
                .resideInAPackage(DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.application.."
                )
                .check(productionClasses);
    }

    @Test
    void domainExceptionShouldBeAbstract() {
        assertThat(
                Modifier.isAbstract(
                        DomainException.class.getModifiers()
                )
        ).isTrue();
    }

    @Test
    void domainExceptionShouldExtendRuntimeException() {
        assertThat(RuntimeException.class)
                .isAssignableFrom(DomainException.class);
    }
}
