package com.shoestore.shared.persistence.mapping;

import com.shoestore.ShoeStoreBackendApplication;
import com.shoestoretest.persistence.mapping.MappingTestDetails;
import com.shoestoretest.persistence.mapping.MappingTestEntity;
import com.shoestoretest.persistence.mapping.MappingTestStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        classes = ShoeStoreBackendApplication.class,
        properties = {
                "spring.flyway.locations="
                        + "classpath:db/migration,"
                        + "classpath:db/mapping-migration"
        }
)
@ActiveProfiles("integration-test")
@Import(JpaMappingIntegrationTest.MappingTestConfiguration.class)
@Transactional
class JpaMappingIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.4.5")
                    .withDatabaseName("shoe_store")
                    .withUsername("shoe_store_app")
                    .withPassword("integration-test-password");

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistAndReloadExplicitJpaMapping() {
        MappingTestEntity entity = new MappingTestEntity(
                "Mapping fixture",
                "Explicit JPA mapping verification",
                MappingTestStatus.ACTIVE,
                new MappingTestDetails(
                        "MAP-REF-001",
                        "Embedded note"
                )
        );

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        MappingTestEntity reloaded = entityManager.find(
                MappingTestEntity.class,
                entity.getId()
        );

        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getDisplayName())
                .isEqualTo("Mapping fixture");
        assertThat(reloaded.getDescription())
                .isEqualTo(
                        "Explicit JPA mapping verification"
                );
        assertThat(reloaded.getStatus())
                .isEqualTo(MappingTestStatus.ACTIVE);
        assertThat(reloaded.getDetails())
                .isEqualTo(
                        new MappingTestDetails(
                                "MAP-REF-001",
                                "Embedded note"
                        )
                );
    }

    @Test
    void shouldPersistNullOptionalColumns() {
        MappingTestEntity entity = new MappingTestEntity(
                "Minimal mapping",
                null,
                MappingTestStatus.INACTIVE,
                new MappingTestDetails(
                        "MAP-REF-002",
                        null
                )
        );

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        MappingTestEntity reloaded = entityManager.find(
                MappingTestEntity.class,
                entity.getId()
        );

        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getDescription()).isNull();
        assertThat(reloaded.getDetails().getNote()).isNull();
    }

    @Test
    void shouldStoreEnumUsingStringRepresentation() {
        MappingTestEntity entity = new MappingTestEntity(
                "Enum mapping",
                null,
                MappingTestStatus.ACTIVE,
                new MappingTestDetails(
                        "MAP-REF-003",
                        null
                )
        );

        entityManager.persist(entity);
        entityManager.flush();

        String storedStatus = (String) entityManager
                .createNativeQuery(
                        """
                        SELECT status
                        FROM mapping_test_entities
                        WHERE id = :id
                        """,
                        String.class
                )
                .setParameter("id", entity.getId())
                .getSingleResult();

        assertThat(storedStatus).isEqualTo("ACTIVE");
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EntityScan(basePackageClasses = MappingTestEntity.class)
    static class MappingTestConfiguration {
    }
}
