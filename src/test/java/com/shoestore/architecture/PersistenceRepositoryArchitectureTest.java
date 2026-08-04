package com.shoestore.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class PersistenceRepositoryArchitectureTest {

    private static final JavaClasses PRODUCTION_CLASSES =
            new ClassFileImporter()
                    .importPackages("com.shoestore");

    @Test
    void sharedApplicationShouldNotDependOnSpringDataRepositories() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.application.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.data.repository..",
                        "org.springframework.data.jpa.repository.."
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedPersistenceEntitiesShouldNotDependOnRepositories() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.entity.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.data.repository..",
                        "org.springframework.data.jpa.repository.."
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedWebShouldNotDependOnSpringDataRepositories() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.web.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.data.repository..",
                        "org.springframework.data.jpa.repository.."
                )
                .check(PRODUCTION_CLASSES);
    }
}
