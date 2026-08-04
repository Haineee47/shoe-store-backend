package com.shoestore.shared.domain.architecture;

import com.shoestore.shared.domain.event.DomainEvent;
import com.shoestore.shared.domain.exception.BusinessRuleViolationException;
import com.shoestore.shared.domain.exception.DomainException;
import com.shoestore.shared.domain.model.AggregateRoot;
import com.shoestore.shared.domain.model.DomainEntity;
import com.shoestore.shared.domain.model.ValueObject;
import com.shoestore.shared.domain.service.DomainService;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class DomainPackageConventionArchitectureTest {

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
    void concreteDomainEntitiesShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(DomainEntity.class)
                .and(isNot(DomainEntity.class))
                .and(isNot(AggregateRoot.class))
                .should()
                .resideInAPackage("..domain.model..")
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void concreteAggregateRootsShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(AggregateRoot.class)
                .and(isNot(AggregateRoot.class))
                .should()
                .resideInAPackage("..domain.model..")
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void valueObjectsShouldResideInModelPackage() {
        classes()
                .that()
                .areAssignableTo(ValueObject.class)
                .and(isNot(ValueObject.class))
                .should()
                .resideInAPackage("..domain.model..")
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void domainEventsShouldResideInEventPackage() {
        classes()
                .that()
                .areAssignableTo(DomainEvent.class)
                .and(isNot(DomainEvent.class))
                .should()
                .resideInAPackage("..domain.event..")
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void domainServicesShouldResideInServicePackage() {
        classes()
                .that()
                .areAssignableTo(DomainService.class)
                .and(isNot(DomainService.class))
                .should()
                .resideInAPackage("..domain.service..")
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void domainExceptionsShouldResideInExceptionPackage() {
        classes()
                .that()
                .areAssignableTo(DomainException.class)
                .should()
                .resideInAPackage("..domain.exception..")
                .check(productionClasses);
    }

    @Test
    void concreteDomainExceptionsShouldUseExceptionSuffix() {
        classes()
                .that()
                .areAssignableTo(DomainException.class)
                .and(isNot(DomainException.class))
                .and(isNot(BusinessRuleViolationException.class))
                .should()
                .haveSimpleNameEndingWith("Exception")
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    @Test
    void domainRepositoriesShouldBeInterfaces() {
        classes()
                .that()
                .resideInAPackage("..domain.repository..")
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .beInterfaces()
                .allowEmptyShould(true)
                .check(productionClasses);
    }

    private static DescribedPredicate<JavaClass> isNot(
            Class<?> excludedType
    ) {
        return new DescribedPredicate<>(
                "not " + excludedType.getName()
        ) {
            @Override
            public boolean test(JavaClass javaClass) {
                return !javaClass.getName()
                        .equals(excludedType.getName());
            }
        };
    }
}
