package com.shoestore.shared.persistence.auditing;

import com.shoestore.ShoeStoreBackendApplication;
import com.shoestoretest.persistence.auditing.AuditingTestEntity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;



import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        classes = ShoeStoreBackendApplication.class,
        properties = {
                "spring.flyway.locations="
                        + "classpath:db/migration,"
                        + "classpath:db/auditing-migration"
        }
)
@ActiveProfiles("integration-test")
@Import(JpaAuditingIntegrationTest.AuditingTestConfiguration.class)
@Transactional
class JpaAuditingIntegrationTest {

    private static final Instant INITIAL_INSTANT =
            Instant.parse("2026-08-03T08:00:00Z");

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.4.5")
                    .withDatabaseName("shoe_store")
                    .withUsername("shoe_store_app")
                    .withPassword("integration-test-password");

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AdjustableClock adjustableClock;

    @BeforeEach
    void resetClock() {
        adjustableClock.setInstant(INITIAL_INSTANT);
    }

    @Test
    void shouldAssignCreationAndModificationTimestampsOnPersist() {
        AuditingTestEntity entity = new AuditingTestEntity("Initial name");

        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
        assertThat(entity.isPersisted()).isFalse();

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getCreatedAt()).isEqualTo(INITIAL_INSTANT);
        assertThat(entity.getUpdatedAt()).isEqualTo(INITIAL_INSTANT);
        assertThat(entity.getVersion()).isNotNull();
        assertThat(entity.isPersisted()).isTrue();
    }

    @Test
    void shouldKeepCreationTimestampAndUpdateModificationTimestamp() {
        AuditingTestEntity entity = new AuditingTestEntity("Initial name");

        entityManager.persist(entity);
        entityManager.flush();

        Instant createdAt = entity.getCreatedAt();
        Instant initialUpdatedAt = entity.getUpdatedAt();

        adjustableClock.advance(Duration.ofMinutes(5));

        entity.rename("Updated name");
        entityManager.flush();

        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getUpdatedAt())
                .isEqualTo(INITIAL_INSTANT.plus(Duration.ofMinutes(5)));
        assertThat(entity.getUpdatedAt()).isAfter(initialUpdatedAt);
    }

    @Test
    void shouldPersistAuditingValuesInMySql() {
        AuditingTestEntity entity =
                new AuditingTestEntity("Database value");

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        AuditingTestEntity reloaded = entityManager.find(
                AuditingTestEntity.class,
                entity.getId()
        );

        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getCreatedAt()).isEqualTo(INITIAL_INSTANT);
        assertThat(reloaded.getUpdatedAt()).isEqualTo(INITIAL_INSTANT);
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EntityScan(basePackageClasses = AuditingTestEntity.class)
    static class AuditingTestConfiguration {

        @Bean
        AdjustableClock adjustableClock() {
            return new AdjustableClock(
                    INITIAL_INSTANT,
                    ZoneOffset.UTC
            );
        }

        @Bean
        @Primary
        Clock auditingTestClock(AdjustableClock adjustableClock) {
            return adjustableClock;
        }
    }

    static final class AdjustableClock extends Clock {

        private Instant currentInstant;
        private final ZoneId zone;

        private AdjustableClock(
                Instant currentInstant,
                ZoneId zone
        ) {
            this.currentInstant = currentInstant;
            this.zone = zone;
        }

        void setInstant(Instant instant) {
            this.currentInstant = instant;
        }

        void advance(Duration duration) {
            currentInstant = currentInstant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId requestedZone) {
            return new AdjustableClock(
                    currentInstant,
                    requestedZone
            );
        }

        @Override
        public Instant instant() {
            return currentInstant;
        }
    }
}
