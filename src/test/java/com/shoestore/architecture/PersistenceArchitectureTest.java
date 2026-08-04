package com.shoestore.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class PersistenceArchitectureTest {

    private static final JavaClasses PRODUCTION_CLASSES =
            new ClassFileImporter()
                    .withImportOption(
                            ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
                    )
                    .importPackages("com.shoestore");

    @Test
    void sharedApplicationShouldNotDependOnPersistenceFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.application.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "org.springframework.data..",
                        "org.springframework.jdbc..",
                        "org.flywaydb..",
                        "javax.sql..",
                        "java.sql.."
                )
                .because(
                        "application contracts and use cases must remain "
                                + "independent from persistence frameworks"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedWebShouldNotDependOnPersistenceFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.web.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "org.springframework.data..",
                        "org.springframework.jdbc..",
                        "org.flywaydb..",
                        "javax.sql..",
                        "java.sql.."
                )
                .because(
                        "web components must not access database or "
                                + "persistence APIs directly"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedWebShouldNotDependOnSharedPersistence() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.web.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.."
                )
                .because(
                        "web must communicate through application "
                                + "boundaries rather than persistence"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedPersistenceShouldNotDependOnWeb() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.web..",
                        "org.springframework.web..",
                        "jakarta.servlet.."
                )
                .because(
                        "persistence infrastructure must remain "
                                + "independent from HTTP and servlet concerns"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedPersistenceShouldNotDependOnSharedApplication() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAPackage(
                        "com.shoestore.shared.application.."
                )
                .because(
                        "shared persistence foundations must not become "
                                + "application-service implementations"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void persistenceEntitiesShouldNotDependOnRepositories() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.entity.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.persistence.repository..",
                        "org.springframework.data.repository..",
                        "org.springframework.data.jpa.repository.."
                )
                .because(
                        "entities must not invoke or own repository behavior"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void persistenceEntitiesShouldNotDependOnConfiguration() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.entity.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.config.."
                )
                .because(
                        "entity models must not depend on Spring "
                                + "configuration classes"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void persistenceConfigurationShouldNotDependOnEntities() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.config.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.entity.."
                )
                .because(
                        "shared persistence configuration should configure "
                                + "framework capabilities rather than "
                                + "specific entity types"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedPersistenceShouldNotDependOnBusinessModules() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.identity..",
                        "com.shoestore.catalog..",
                        "com.shoestore.inventory..",
                        "com.shoestore.cart..",
                        "com.shoestore.address..",
                        "com.shoestore.promotion..",
                        "com.shoestore.order..",
                        "com.shoestore.payment..",
                        "com.shoestore.shipping..",
                        "com.shoestore.administration..",
                        "com.shoestore.reporting.."
                )
                .because(
                        "Shared must not depend on business-module code"
                )
                .allowEmptyShould(true)
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void productionCodeShouldNotDependOnTestFrameworks() {
        noClasses()
                .that()
                .resideInAPackage("com.shoestore..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.junit..",
                        "org.assertj..",
                        "org.mockito..",
                        "org.testcontainers.."
                )
                .because(
                        "production code must not depend on test-only APIs"
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedPersistenceTopLevelPackagesShouldBeAcyclic() {
        slices()
                .matching(
                        "com.shoestore.shared.persistence.(*).."
                )
                .should()
                .beFreeOfCycles()
                .because(
                        "persistence entity, configuration, mapping, "
                                + "repository and schema foundations must "
                                + "not form package cycles"
                )
                .check(PRODUCTION_CLASSES);
    }
}
