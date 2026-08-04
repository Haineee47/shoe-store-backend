package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.event.DomainEvent;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DomainEventDependencyArchitectureTest {

    private final JavaClasses productionClasses =
            new ClassFileImporter()
                    .importPackages("com.shoestore");

    @Test
    void domainEventsShouldNotDependOnFrameworks() {
        noClasses()
                .that()
                .implement(DomainEvent.class)
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
    void domainEventsShouldNotDependOnApplicationOrWeb() {
        noClasses()
                .that()
                .implement(DomainEvent.class)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.application..",
                        "com.shoestore.shared.web.."
                )
                .check(productionClasses);
    }

    @Test
    void domainEventsShouldNotDependOnPersistenceOrMessaging() {
        noClasses()
                .that()
                .implement(DomainEvent.class)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.shared.persistence..",
                        "java.sql..",
                        "org.apache.kafka..",
                        "com.rabbitmq.."
                )
                .check(productionClasses);
    }
}
