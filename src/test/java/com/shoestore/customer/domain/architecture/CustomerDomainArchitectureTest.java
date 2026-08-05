package com.shoestore.customer.domain.architecture;

import com.shoestore.customer.domain.model.Customer;
import com.shoestore.customer.domain.model.CustomerId;
import com.shoestore.customer.domain.model.CustomerName;
import com.shoestore.customer.domain.model.CustomerStatus;
import com.shoestore.customer.domain.model.EmailAddress;
import com.shoestore.customer.domain.repository.CustomerRepository;
import com.shoestore.shared.domain.model.AggregateRoot;
import com.shoestore.shared.domain.model.ValueObject;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CustomerDomainArchitectureTest {

    private static final String CUSTOMER_DOMAIN_PACKAGE =
            "com.shoestore.customer.domain..";

    private static final JavaClasses CUSTOMER_DOMAIN_CLASSES =
            new ClassFileImporter()
                    .importPackages("com.shoestore.customer.domain");

    @Test
    void customerDomainShouldRemainFrameworkIndependent() {
        noClasses()
                .that()
                .resideInAPackage(CUSTOMER_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "com.fasterxml.jackson..",
                        "jakarta.validation..",
                        "org.slf4j..",
                        "ch.qos.logback.."
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerDomainShouldNotDependOnOuterLayers() {
        noClasses()
                .that()
                .resideInAPackage(CUSTOMER_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "..web..",
                        "..application..",
                        "..persistence..",
                        "..infrastructure.."
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerDomainShouldNotDependOnSecurityPackages() {
        noClasses()
                .that()
                .resideInAPackage(CUSTOMER_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.security..",
                        "..security..",
                        "..authentication..",
                        "..authorization.."
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerDomainShouldNotDependOnOtherBusinessModules() {
        noClasses()
                .that()
                .resideInAPackage(CUSTOMER_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.category..",
                        "com.shoestore.product..",
                        "com.shoestore.inventory..",
                        "com.shoestore.cart..",
                        "com.shoestore.order.."
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(Customer.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.customer.domain.model"
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerIdShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(CustomerId.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.customer.domain.model"
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerNameShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(CustomerName.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.customer.domain.model"
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void emailAddressShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(EmailAddress.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.customer.domain.model"
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerStatusShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(CustomerStatus.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.customer.domain.model"
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerShouldBeAggregateRoot() {
        classes()
                .that()
                .areAssignableTo(Customer.class)
                .should()
                .beAssignableTo(AggregateRoot.class)
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerIdShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(CustomerId.class)
                .should()
                .implement(ValueObject.class)
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerNameShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(CustomerName.class)
                .should()
                .implement(ValueObject.class)
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void emailAddressShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(EmailAddress.class)
                .should()
                .implement(ValueObject.class)
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerRepositoryShouldResideInRepositoryPackage() {
        classes()
                .that()
                .areAssignableTo(CustomerRepository.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.customer.domain.repository"
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerRepositoriesShouldBeInterfaces() {
        classes()
                .that()
                .resideInAPackage(
                        "com.shoestore.customer.domain.repository"
                )
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .beInterfaces()
                .check(CUSTOMER_DOMAIN_CLASSES);
    }

    @Test
    void customerRepositoryShouldNotDependOnPersistenceFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.customer.domain.repository"
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.data..",
                        "jakarta.persistence..",
                        "org.hibernate.."
                )
                .check(CUSTOMER_DOMAIN_CLASSES);
    }
}
