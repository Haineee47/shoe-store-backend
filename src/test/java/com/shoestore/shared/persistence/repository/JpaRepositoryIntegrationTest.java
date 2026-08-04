package com.shoestore.shared.persistence.repository;

import com.shoestore.ShoeStoreBackendApplication;
import com.shoestoretest.persistence.repository.RepositoryTestEntity;
import com.shoestoretest.persistence.repository.RepositoryTestJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        classes = ShoeStoreBackendApplication.class,
        properties = {
                "spring.flyway.locations="
                        + "classpath:db/migration,"
                        + "classpath:db/repository-migration"
        }
)
@ActiveProfiles("integration-test")
@Import(JpaRepositoryIntegrationTest.RepositoryTestConfiguration.class)
@Transactional
class JpaRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.4.5")
                    .withDatabaseName("shoe_store")
                    .withUsername("shoe_store_app")
                    .withPassword("integration-test-password");

    @Autowired
    private RepositoryTestJpaRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldInsertEntityWhoseIdentifierAlreadyExistsBeforeSave() {
        RepositoryTestEntity entity =
                new RepositoryTestEntity("Initial name");

        UUID identifierBeforeSave = entity.getId();

        assertThat(identifierBeforeSave).isNotNull();
        assertThat(entity.getVersion()).isNull();
        assertThat(entity.isPersisted()).isFalse();

        RepositoryTestEntity saved = repository.saveAndFlush(entity);

        assertThat(saved).isSameAs(entity);
        assertThat(saved.getId()).isEqualTo(identifierBeforeSave);
        assertThat(saved.getVersion()).isNotNull();
        assertThat(saved.isPersisted()).isTrue();
    }

    @Test
    void shouldReloadSavedEntityFromMySql() {
        RepositoryTestEntity entity =
                new RepositoryTestEntity("Persisted name");

        repository.saveAndFlush(entity);
        entityManager.clear();

        RepositoryTestEntity reloaded = repository.findById(entity.getId())
                .orElseThrow();

        assertThat(reloaded.getId()).isEqualTo(entity.getId());
        assertThat(reloaded.getName()).isEqualTo("Persisted name");
        assertThat(reloaded.getVersion()).isNotNull();
    }

    @Test
    void shouldUpdateExistingEntityAndIncrementVersion() {
        RepositoryTestEntity entity =
                new RepositoryTestEntity("Initial name");

        repository.saveAndFlush(entity);

        Long initialVersion = entity.getVersion();

        entity.rename("Updated name");
        repository.flush();

        assertThat(entity.getName()).isEqualTo("Updated name");
        assertThat(entity.getVersion()).isGreaterThan(initialVersion);

        entityManager.clear();

        RepositoryTestEntity reloaded = repository.findById(entity.getId())
                .orElseThrow();

        assertThat(reloaded.getName()).isEqualTo("Updated name");
        assertThat(reloaded.getVersion())
                .isEqualTo(entity.getVersion());
    }

    @Test
    void shouldReturnEmptyWhenIdentifierDoesNotExist() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EntityScan(basePackageClasses = RepositoryTestEntity.class)
    @EnableJpaRepositories(
            basePackageClasses = RepositoryTestJpaRepository.class
    )
    static class RepositoryTestConfiguration {
    }
}
