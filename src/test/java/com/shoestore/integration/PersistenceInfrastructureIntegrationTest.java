package com.shoestore.integration;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("integration-test")
@SpringBootTest
class PersistenceInfrastructureIntegrationTest {

    private static final DockerImageName MYSQL_IMAGE =
            DockerImageName.parse("mysql:8.4.5");

    @Container
    @ServiceConnection
    private static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>(MYSQL_IMAGE)
                    .withDatabaseName("shoe_store_test")
                    .withUsername("shoe_store_test")
                    .withPassword("test-password");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Flyway flyway;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void shouldStartMySqlContainer() {
        assertThat(MYSQL.isRunning()).isTrue();
        assertThat(MYSQL.getDockerImageName()).isEqualTo("mysql:8.4.5");
    }

    @Test
    void shouldConnectToMySql845() {
        String databaseVersion =
                jdbcTemplate.queryForObject("SELECT VERSION()", String.class);

        assertThat(databaseVersion).startsWith("8.4.5");
    }

    @Test
    void shouldApplyFlywayMigration() {
        assertThat(flyway.info().current()).isNotNull();
        assertThat(flyway.info().current().getVersion().getVersion())
                .isEqualTo("1");
        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    void shouldCreateOnlyFlywaySchemaHistoryTable() {
        var tables = jdbcTemplate.queryForList(
                """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                ORDER BY table_name
                """,
                String.class
        );

        assertThat(tables)
                .containsExactly("flyway_schema_history");
    }

    @Test
    void shouldConnectWithNonRootApplicationUser() {
        String currentUser =
                jdbcTemplate.queryForObject("SELECT CURRENT_USER()", String.class);

        assertThat(currentUser)
                .isNotBlank()
                .doesNotStartWith("root@");
    }

    @Test
    void shouldInitializeJpaEntityManagerFactory() {
        assertThat(entityManagerFactory).isNotNull();
        assertThat(entityManagerFactory.isOpen()).isTrue();
    }

    @Test
    void shouldConfigureDataSource() throws Exception {
        try (var connection = dataSource.getConnection()) {
            assertThat(connection.isValid(5)).isTrue();
            assertThat(connection.getCatalog()).isEqualTo("shoe_store_test");
        }
    }
}
