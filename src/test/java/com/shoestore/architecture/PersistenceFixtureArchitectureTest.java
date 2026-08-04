package com.shoestore.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition
        .classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition
        .noClasses;

class PersistenceFixtureArchitectureTest {

    /*
     * This test intentionally imports test classes because its purpose is to
     * validate test-fixture architecture.
     */
    private static final JavaClasses TEST_AND_PRODUCTION_CLASSES =
            new ClassFileImporter()
                    .importPackages(
                            "com.shoestore",
                            "com.shoestoretest"
                    );

    @Test
    void testEntitiesShouldResideInIsolatedFixturePackage() {
        classes()
                .that()
                .areAnnotatedWith(Entity.class)
                .and()
                .haveSimpleNameEndingWith("TestEntity")
                .should()
                .resideInAPackage(
                        "com.shoestoretest.persistence.."
                )
                .because(
                        "test entities must not be discovered by the "
                                + "application's default entity scan"
                )
                .check(TEST_AND_PRODUCTION_CLASSES);
    }

    @Test
    void isolatedPersistenceFixturesShouldNotDependOnWeb() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestoretest.persistence.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.web..",
                        "org.springframework.web..",
                        "jakarta.servlet.."
                )
                .because(
                        "persistence fixtures model database behavior "
                                + "and must remain independent from web code"
                )
                .check(TEST_AND_PRODUCTION_CLASSES);
    }

    @Test
    void isolatedPersistenceFixturesShouldNotDependOnApplicationLayer() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestoretest.persistence.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAPackage(
                        "com.shoestore.shared.application.."
                )
                .because(
                        "technical persistence fixtures must not become "
                                + "application or business fixtures"
                )
                .check(TEST_AND_PRODUCTION_CLASSES);
    }
}
