package com.shoestore.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import java.time.Instant;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Protects the dependency boundaries of the shared foundation.
 *
 * <p>These rules intentionally focus on boundaries that currently exist.
 * Business-module and domain-specific rules must be introduced only after
 * those production packages are created.</p>
 */
@AnalyzeClasses(
        packages = "com.shoestore",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class SharedArchitectureTest {

    private static final String SHARED_APPLICATION =
            "..shared.application..";

    private static final String SHARED_WEB =
            "..shared.web..";

    private static final String SHARED_INFRASTRUCTURE =
            "..shared.infrastructure..";

    @ArchTest
    static final ArchRule sharedApplicationShouldNotDependOnSharedWeb =
            noClasses()
                    .that()
                    .resideInAPackage(SHARED_APPLICATION)
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage(SHARED_WEB)
                    .because(
                            "application contracts must remain independent "
                                    + "of the web boundary"
                    );

    @ArchTest
    static final ArchRule sharedApplicationShouldNotDependOnInfrastructure =
            noClasses()
                    .that()
                    .resideInAPackage(SHARED_APPLICATION)
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage(SHARED_INFRASTRUCTURE)
                    .because(
                            "application contracts must not depend on "
                                    + "technical configuration or adapters"
                    );

    @ArchTest
    static final ArchRule sharedApplicationShouldNotDependOnWebFrameworks =
            noClasses()
                    .that()
                    .resideInAPackage(SHARED_APPLICATION)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework.web..",
                            "org.springframework.http..",
                            "jakarta.servlet.."
                    )
                    .because(
                            "HTTP translation belongs to the web boundary"
                    );

    @ArchTest
    static final ArchRule
            sharedApplicationShouldNotDependOnPersistenceFrameworks =
            noClasses()
                    .that()
                    .resideInAPackage(SHARED_APPLICATION)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "jakarta.persistence..",
                            "org.hibernate..",
                            "org.springframework.data.."
                    )
                    .because(
                            "shared application contracts must remain "
                                    + "persistence-framework independent"
                    );

    @ArchTest
    static final ArchRule sharedWebShouldNotDependOnInfrastructure =
            noClasses()
                    .that()
                    .resideInAPackage(SHARED_WEB)
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage(SHARED_INFRASTRUCTURE)
                    .because(
                            "web components should depend on abstractions "
                                    + "such as Clock, not configuration classes"
                    );

    @ArchTest
    static final ArchRule sharedInfrastructureShouldNotDependOnWeb =
            noClasses()
                    .that()
                    .resideInAPackage(SHARED_INFRASTRUCTURE)
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage(SHARED_WEB)
                    .because(
                            "infrastructure configuration must not depend "
                                    + "on HTTP presentation components"
                    );

    @ArchTest
    static final ArchRule sharedFoundationShouldNotDependOnBusinessModules =
            noClasses()
                    .that()
                    .resideInAPackage("..shared..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "..identity..",
                            "..catalog..",
                            "..inventory..",
                            "..cart..",
                            "..address..",
                            "..promotion..",
                            "..order..",
                            "..payment..",
                            "..shipping..",
                            "..administration..",
                            "..reporting..",
                            "..modules.."
                    )
                    .because(
                            "shared foundation must never depend on "
                                    + "business-owned code"
                    );

    @ArchTest
    static final ArchRule sharedWebShouldNotOwnTransactions =
            noClasses()
                    .that()
                    .resideInAPackage(SHARED_WEB)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework.transaction.."
                    )
                    .because(
                            "transaction boundaries belong to application "
                                    + "use-case implementations"
                    );

    @ArchTest
    static final ArchRule sharedProductionCodeShouldNotCallInstantNow =
            noClasses()
                    .that()
                    .resideInAPackage("..shared..")
                    .should()
                    .callMethod(
                            Instant.class,
                            "now"
                    )
                    .because(
                            "current time must come from an injected Clock"
                    );

    @ArchTest
    static final ArchRule sharedTopLevelPackagesShouldBeFreeOfCycles =
            SlicesRuleDefinition
                    .slices()
                    .matching("com.shoestore.shared.(*)..")
                    .should()
                    .beFreeOfCycles()
                    .because(
                            "application, web and infrastructure must not "
                                    + "form circular dependencies"
                    );
}
