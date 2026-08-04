package com.shoestore.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition
        .noClasses;

class JpaMappingArchitectureTest {

    private static final JavaClasses PRODUCTION_CLASSES =
            new ClassFileImporter()
                    .importPackages("com.shoestore");

    @Test
    void sharedApplicationShouldNotDependOnJpaMapping() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.application.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.hibernate.."
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedWebShouldNotDependOnJpaMapping() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.web.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.hibernate.."
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedPersistenceMappingShouldNotContainWebDependencies() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.mapping.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.web..",
                        "jakarta.servlet.."
                )
                .check(PRODUCTION_CLASSES);
    }
}
