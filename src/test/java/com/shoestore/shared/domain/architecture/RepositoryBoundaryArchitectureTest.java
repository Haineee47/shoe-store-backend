package com.shoestore.shared.domain.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class RepositoryBoundaryArchitectureTest {

    private final JavaClasses productionClasses =
            new ClassFileImporter()
                    .importPackages("com.shoestore");

    @Test
    void domainRepositoriesShouldBeInterfaces() {
        classes()
                .that()
                .resideInAPackage(
                        "..domain.repository.."
                )
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .beInterfaces()
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void domainRepositoriesShouldNotDependOnPersistenceFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(
                        "..domain.repository.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "org.springframework.data..",
                        "java.sql.."
                )
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void domainRepositoriesShouldNotDependOnWeb() {
        noClasses()
                .that()
                .resideInAPackage(
                        "..domain.repository.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "..web..",
                        "jakarta.servlet.."
                )
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void domainRepositoriesShouldNotDependOnSharedPersistence() {
        noClasses()
                .that()
                .resideInAPackage(
                        "..domain.repository.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.persistence.."
                )
                .allowEmptyShould(true)
                .check(productionClasses);
    }
}
