package com.shoestore.category.domain.architecture;

import com.shoestore.category.domain.model.Category;
import com.shoestore.category.domain.model.CategoryDescription;
import com.shoestore.category.domain.model.CategoryId;
import com.shoestore.category.domain.model.CategoryName;
import com.shoestore.category.domain.repository.CategoryRepository;
import com.shoestore.shared.domain.model.AggregateRoot;
import com.shoestore.shared.domain.model.ValueObject;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CategoryDomainArchitectureTest {

    private static final String CATEGORY_DOMAIN_PACKAGE =
            "com.shoestore.category.domain..";

    private static final JavaClasses CATEGORY_DOMAIN_CLASSES =
            new ClassFileImporter()
                    .importPackages("com.shoestore.category.domain");

    @Test
    void categoryDomainShouldRemainFrameworkIndependent() {
        noClasses()
                .that()
                .resideInAPackage(CATEGORY_DOMAIN_PACKAGE)
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
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryDomainShouldNotDependOnOuterLayers() {
        noClasses()
                .that()
                .resideInAPackage(CATEGORY_DOMAIN_PACKAGE)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "..web..",
                        "..application..",
                        "..persistence..",
                        "..infrastructure.."
                )
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(Category.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.category.domain.model"
                )
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryIdShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(CategoryId.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.category.domain.model"
                )
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryNameShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(CategoryName.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.category.domain.model"
                )
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryDescriptionShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(CategoryDescription.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.category.domain.model"
                )
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryShouldBeAggregateRoot() {
        classes()
                .that()
                .areAssignableTo(Category.class)
                .should()
                .beAssignableTo(AggregateRoot.class)
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryIdShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(CategoryId.class)
                .should()
                .implement(ValueObject.class)
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryNameShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(CategoryName.class)
                .should()
                .implement(ValueObject.class)
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryDescriptionShouldImplementValueObject() {
        classes()
                .that()
                .areAssignableTo(CategoryDescription.class)
                .should()
                .implement(ValueObject.class)
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryRepositoryShouldResideInRepositoryPackage() {
        classes()
                .that()
                .areAssignableTo(CategoryRepository.class)
                .should()
                .resideInAPackage(
                        "com.shoestore.category.domain.repository"
                )
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryRepositoriesShouldBeInterfaces() {
        classes()
                .that()
                .resideInAPackage(
                        "com.shoestore.category.domain.repository"
                )
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .beInterfaces()
                .check(CATEGORY_DOMAIN_CLASSES);
    }

    @Test
    void categoryRepositoryShouldNotExtendExternalRepositoryTypes() {
        noClasses()
                .that()
                .resideInAPackage(
                        "com.shoestore.category.domain.repository"
                )
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.data..",
                        "jakarta.persistence..",
                        "org.hibernate.."
                )
                .check(CATEGORY_DOMAIN_CLASSES);
    }
}
