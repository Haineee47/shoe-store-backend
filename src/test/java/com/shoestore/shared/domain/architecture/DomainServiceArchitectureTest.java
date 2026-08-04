package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.service.DomainService;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DomainServiceDependencyArchitectureTest {

    private final JavaClasses productionClasses =
            new ClassFileImporter()
                    .importPackages("com.shoestore");

    @Test
    void domainServicesShouldNotDependOnFrameworks() {
        noClasses()
                .that()
                .implement(DomainService.class)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "org.springframework.data..",
                        "jakarta.servlet.."
                )
                .check(productionClasses);
    }

    @Test
    void domainServicesShouldNotDependOnApplicationOrWeb() {
        noClasses()
                .that()
                .implement(DomainService.class)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.application..",
                        "com.shoestore.shared.web.."
                )
                .check(productionClasses);
    }

    @Test
    void domainServicesShouldNotDependOnPersistence() {
        noClasses()
                .that()
                .implement(DomainService.class)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.persistence..",
                        "java.sql.."
                )
                .check(productionClasses);
    }
}
