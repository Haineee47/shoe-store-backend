package com.shoestore.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition
        .noClasses;

class MySqlSchemaArchitectureTest {

    private static final JavaClasses PRODUCTION_CLASSES =
            new ClassFileImporter()
                    .importPackages("com.shoestore");

    @Test
    void sharedApplicationShouldNotDependOnDatabaseSchemaApis() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.application.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "java.sql..",
                        "javax.sql..",
                        "org.springframework.jdbc..",
                        "org.flywaydb.."
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedWebShouldNotDependOnDatabaseSchemaApis() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.web.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "java.sql..",
                        "javax.sql..",
                        "org.springframework.jdbc..",
                        "org.flywaydb.."
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedSchemaPackageShouldNotDependOnWeb() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.schema.."
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.web..",
                        "jakarta.servlet.."
                )
                .check(PRODUCTION_CLASSES);
    }

    @Test
    void sharedSchemaPackageShouldNotDependOnBusinessModules() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.shared.persistence.schema.."
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
                .check(PRODUCTION_CLASSES);
    }
}
