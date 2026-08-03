package com.shoestore.shared.application.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionBoundaryConventionTest {

    private final AnnotationTransactionAttributeSource attributeSource =
            new AnnotationTransactionAttributeSource();

    @Test
    void writeUseCaseShouldUseRequiredReadWriteTransaction()
            throws NoSuchMethodException {
        Method method = WriteUseCaseFixture.class.getDeclaredMethod(
                "execute"
        );

        TransactionAttribute attribute =
                attributeSource.getTransactionAttribute(
                        method,
                        WriteUseCaseFixture.class
                );

        assertThat(attribute).isNotNull();

        assertThat(attribute.getPropagationBehavior())
                .isEqualTo(
                        Propagation.REQUIRED.value()
                );

        assertThat(attribute.isReadOnly()).isFalse();
    }

    @Test
    void queryUseCaseShouldUseRequiredReadOnlyTransaction()
            throws NoSuchMethodException {
        Method method = QueryUseCaseFixture.class.getDeclaredMethod(
                "execute"
        );

        TransactionAttribute attribute =
                attributeSource.getTransactionAttribute(
                        method,
                        QueryUseCaseFixture.class
                );

        assertThat(attribute).isNotNull();

        assertThat(attribute.getPropagationBehavior())
                .isEqualTo(
                        Propagation.REQUIRED.value()
                );

        assertThat(attribute.isReadOnly()).isTrue();
    }

    @Test
    void controllerFixtureShouldNotOwnTransaction()
            throws NoSuchMethodException {
        Method method = ControllerFixture.class.getDeclaredMethod(
                "handle"
        );

        TransactionAttribute attribute =
                attributeSource.getTransactionAttribute(
                        method,
                        ControllerFixture.class
                );

        assertThat(attribute).isNull();
    }

    @Test
    void domainFixtureShouldRemainTransactionAgnostic()
            throws NoSuchMethodException {
        Method method = DomainFixture.class.getDeclaredMethod(
                "performBusinessOperation"
        );

        TransactionAttribute attribute =
                attributeSource.getTransactionAttribute(
                        method,
                        DomainFixture.class
                );

        assertThat(attribute).isNull();

        assertThat(DomainFixture.class.getAnnotations())
                .noneMatch(annotation ->
                        annotation.annotationType()
                                .equals(Transactional.class)
                );
    }

    @Test
    void repositoryPortFixtureShouldNotOwnTransaction()
            throws NoSuchMethodException {
        Method method = RepositoryPortFixture.class.getDeclaredMethod(
                "save",
                DomainFixture.class
        );

        TransactionAttribute attribute =
                attributeSource.getTransactionAttribute(
                        method,
                        RepositoryPortFixture.class
                );

        assertThat(attribute).isNull();

        assertThat(RepositoryPortFixture.class.getAnnotations())
                .noneMatch(annotation ->
                        annotation.annotationType()
                                .equals(Transactional.class)
                );
    }

    @Test
    void persistenceAdapterShouldParticipateWithoutDeclaringNewTransaction()
            throws NoSuchMethodException {
        Method method = PersistenceAdapterFixture.class.getDeclaredMethod(
                "save",
                DomainFixture.class
        );

        TransactionAttribute attribute =
                attributeSource.getTransactionAttribute(
                        method,
                        PersistenceAdapterFixture.class
                );

        assertThat(attribute).isNull();
    }

    @Test
    void useCasePortShouldNotExposeSpringTransactionPolicy()
            throws NoSuchMethodException {
        Method method = WriteUseCasePortFixture.class.getDeclaredMethod(
                "execute"
        );

        TransactionAttribute attribute =
                attributeSource.getTransactionAttribute(
                        method,
                        WriteUseCasePortFixture.class
                );

        assertThat(attribute).isNull();

        assertThat(WriteUseCasePortFixture.class.getAnnotations())
                .noneMatch(annotation ->
                        annotation.annotationType()
                                .equals(Transactional.class)
                );
    }

    @Transactional
    static class WriteUseCaseFixture
            implements WriteUseCasePortFixture {

        @Override
        public void execute() {
            // Transaction boundary fixture only.
        }
    }

    @Transactional(readOnly = true)
    static class QueryUseCaseFixture {

        public Object execute() {
            return new Object();
        }
    }

    interface WriteUseCasePortFixture {

        void execute();
    }

    static class ControllerFixture {

        public void handle() {
            // Web boundary fixture only.
        }
    }

    static class DomainFixture {

        public void performBusinessOperation() {
            // Domain behavior fixture only.
        }
    }

    interface RepositoryPortFixture {

        DomainFixture save(DomainFixture aggregate);
    }

    static class PersistenceAdapterFixture
            implements RepositoryPortFixture {

        @Override
        public DomainFixture save(DomainFixture aggregate) {
            return aggregate;
        }
    }
}
