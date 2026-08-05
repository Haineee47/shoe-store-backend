package com.shoestore.product.domain.architecture;

import com.shoestore.product.domain.model.Product;
import com.shoestore.product.domain.model.ProductDescription;
import com.shoestore.product.domain.model.ProductId;
import com.shoestore.product.domain.model.ProductName;
import com.shoestore.product.domain.model.ProductSku;
import com.shoestore.product.domain.repository.ProductRepository;
import com.shoestore.shared.domain.model.AggregateRoot;
import com.shoestore.shared.domain.model.ValueObject;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.shoestore.product.domain.model.ProductStatus;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ProductDomainArchitectureTest {

    private static final String PRODUCT_DOMAIN_PACKAGE =
            "com.shoestore.product.domain..";

    private static final JavaClasses PRODUCT_DOMAIN_CLASSES =
            new ClassFileImporter()
                    .importPackages("com.shoestore.product.domain");

    @Test
    void productDomainShouldRemainFrameworkIndependent() {
        noClasses()
                .that()
                .resideInAPackage(PRODUCT_DOMAIN_PACKAGE)
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
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productDomainShouldNotDependOnOuterLayers() {
        noClasses()
                .that()
                .resideInAPackage(PRODUCT_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "..web..",
                        "..application..",
                        "..persistence..",
                        "..infrastructure.."
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productDomainShouldNotDependOnOtherBusinessModules() {
        noClasses()
                .that()
                .resideInAPackage(PRODUCT_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.shoestore.category..",
                        "com.shoestore.inventory..",
                        "com.shoestore.cart..",
                        "com.shoestore.order..",
                        "com.shoestore.customer.."
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(Product.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.product.domain.model"
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productIdShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(ProductId.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.product.domain.model"
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productNameShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(ProductName.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.product.domain.model"
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productDescriptionShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(ProductDescription.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.product.domain.model"
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productSkuShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(ProductSku.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.product.domain.model"
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productShouldBeAggregateRoot() {
        classes()
                .that()
                .areAssignableTo(Product.class)
                .should()
                .beAssignableTo(AggregateRoot.class)
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productIdShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(ProductId.class)
                .should()
                .implement(ValueObject.class)
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productNameShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(ProductName.class)
                .should()
                .implement(ValueObject.class)
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productDescriptionShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(ProductDescription.class)
                .should()
                .implement(ValueObject.class)
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productSkuShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(ProductSku.class)
                .should()
                .implement(ValueObject.class)
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productRepositoryShouldResideInRepositoryPackage() {
        classes()
                .that()
                .areAssignableTo(ProductRepository.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.product.domain.repository"
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productRepositoriesShouldBeInterfaces() {
        classes()
                .that()
                .resideInAPackage(
                        "com.shoestore.product.domain.repository"
                )
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .beInterfaces()
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productRepositoryShouldNotDependOnPersistenceFrameworks() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.product.domain.repository")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.data..",
                        "jakarta.persistence..",
                        "org.hibernate..")
                .check(PRODUCT_DOMAIN_CLASSES);
    }

    @Test
    void productStatusShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(ProductStatus.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.product.domain.model"
                )
                .check(PRODUCT_DOMAIN_CLASSES);
    }
}
