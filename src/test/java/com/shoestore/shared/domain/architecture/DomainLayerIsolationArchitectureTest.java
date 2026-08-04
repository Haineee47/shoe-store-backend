package com.shoestore.shared.domain.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DomainLayerIsolationArchitectureTest {

    private static final String ANY_DOMAIN =
            "com.shoestore..domain..";

    private final JavaClasses productionClasses =
            new ClassFileImporter()
                    .withImportOption(
                            ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
                    )
                    .withImportOption(
                            ImportOption.Predefined.DO_NOT_INCLUDE_JARS
                    )
                    .importPackages("com.shoestore");

    @Test
    void domainShouldNotDependOnOuterLayers() {
        noClasses()
                .that()
                .resideInAPackage(ANY_DOMAIN)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "..application..",
                        "..web..",
                        "..controller..",
                        "..persistence..",
                        "..infrastructure.."
                )
                .check(productionClasses);
    }

    @Test
    void domainShouldNotDependOnFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(ANY_DOMAIN)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "org.springframework.data..",
                        "org.flywaydb..",
                        "java.sql.."
                )
                .check(productionClasses);
    }

    @Test
    void domainShouldNotDependOnTransportOrMessaging() {
        noClasses()
                .that()
                .resideInAPackage(ANY_DOMAIN)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.servlet..",
                        "com.fasterxml.jackson..",
                        "io.swagger.v3..",
                        "org.apache.kafka..",
                        "com.rabbitmq..",
                        "org.springframework.amqp..",
                        "org.springframework.kafka.."
                )
                .check(productionClasses);
    }

    @Test
    void domainShouldNotDependOnBeanValidation() {
        noClasses()
                .that()
                .resideInAPackage(ANY_DOMAIN)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.validation.."
                )
                .check(productionClasses);
    }

    @Test
    void domainShouldNotDependOnLoggingFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(ANY_DOMAIN)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.slf4j..",
                        "ch.qos.logback..",
                        "org.apache.logging.."
                )
                .check(productionClasses);
    }

    @Test
    void productionDomainShouldNotDependOnTestLibraries() {
        noClasses()
                .that()
                .resideInAPackage(ANY_DOMAIN)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.junit..",
                        "org.assertj..",
                        "org.mockito..",
                        "org.testcontainers..",
                        "com.tngtech.archunit.."
                )
                .check(productionClasses);
    }
}
