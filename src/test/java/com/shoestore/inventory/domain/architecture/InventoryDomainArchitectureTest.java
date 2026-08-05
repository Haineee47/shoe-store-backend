package com.shoestore.inventory.domain.architecture;

import com.shoestore.inventory.domain.exception.InsufficientAvailableStockException;
import com.shoestore.inventory.domain.exception.InsufficientReservedStockException;
import com.shoestore.inventory.domain.exception.StockQuantityOverflowException;
import com.shoestore.inventory.domain.model.Inventory;
import com.shoestore.inventory.domain.model.InventoryId;
import com.shoestore.inventory.domain.model.StockAmount;
import com.shoestore.inventory.domain.model.StockQuantity;
import com.shoestore.inventory.domain.repository.InventoryRepository;
import com.shoestore.shared.domain.exception.BusinessRuleViolationException;
import com.shoestore.shared.domain.model.AggregateRoot;
import com.shoestore.shared.domain.model.ValueObject;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class InventoryDomainArchitectureTest {

    private static final String INVENTORY_DOMAIN_PACKAGE =
            "com.shoestore.inventory.domain..";

    private static final JavaClasses INVENTORY_DOMAIN_CLASSES =
            new ClassFileImporter()
                    .importPackages("com.shoestore.inventory.domain");

    @Test
    void inventoryDomainShouldRemainFrameworkIndependent() {
        noClasses()
                .that()
                .resideInAPackage(INVENTORY_DOMAIN_PACKAGE)
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
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryDomainShouldNotDependOnOuterLayers() {
        noClasses()
                .that()
                .resideInAPackage(INVENTORY_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "..web..",
                        "..application..",
                        "..persistence..",
                        "..infrastructure.."
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryDomainShouldNotDependOnOtherBusinessModules() {
        noClasses()
                .that()
                .resideInAPackage(INVENTORY_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.category..",
                        "com.shoestore.product..",
                        "com.shoestore.cart..",
                        "com.shoestore.order..",
                        "com.shoestore.customer.."
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(Inventory.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.model"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryIdShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(InventoryId.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.model"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void stockAmountShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(StockAmount.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.model"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void stockQuantityShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(StockQuantity.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.model"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryShouldBeAggregateRoot() {
        classes()
                .that()
                .areAssignableTo(Inventory.class)
                .should()
                .beAssignableTo(AggregateRoot.class)
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryIdShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(InventoryId.class)
                .should()
                .implement(ValueObject.class)
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void stockAmountShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(StockAmount.class)
                .should()
                .implement(ValueObject.class)
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void stockQuantityShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(StockQuantity.class)
                .should()
                .implement(ValueObject.class)
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void availableStockExceptionShouldResideInExceptionPackage() {
        classes()
                .that()
                .areAssignableTo(
                        InsufficientAvailableStockException.class
                )
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.exception"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void reservedStockExceptionShouldResideInExceptionPackage() {
        classes()
                .that()
                .areAssignableTo(
                        InsufficientReservedStockException.class
                )
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.exception"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void stockOverflowExceptionShouldResideInExceptionPackage() {
        classes()
                .that()
                .areAssignableTo(
                        StockQuantityOverflowException.class
                )
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.exception"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void availableStockExceptionShouldBeBusinessRuleViolation() {
        classes()
                .that()
                .areAssignableTo(
                        InsufficientAvailableStockException.class
                )
                .should()
                .beAssignableTo(
                        BusinessRuleViolationException.class
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void reservedStockExceptionShouldBeBusinessRuleViolation() {
        classes()
                .that()
                .areAssignableTo(
                        InsufficientReservedStockException.class
                )
                .should()
                .beAssignableTo(
                        BusinessRuleViolationException.class
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void stockOverflowExceptionShouldBeBusinessRuleViolation() {
        classes()
                .that()
                .areAssignableTo(
                        StockQuantityOverflowException.class
                )
                .should()
                .beAssignableTo(
                        BusinessRuleViolationException.class
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryRepositoryShouldResideInRepositoryPackage() {
        classes()
                .that()
                .areAssignableTo(InventoryRepository.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.repository"
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryRepositoriesShouldBeInterfaces() {
        classes()
                .that()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.repository"
                )
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .beInterfaces()
                .check(INVENTORY_DOMAIN_CLASSES);
    }

    @Test
    void inventoryRepositoryShouldNotDependOnPersistenceFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.inventory.domain.repository"
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.data..",
                        "jakarta.persistence..",
                        "org.hibernate.."
                )
                .check(INVENTORY_DOMAIN_CLASSES);
    }
}
