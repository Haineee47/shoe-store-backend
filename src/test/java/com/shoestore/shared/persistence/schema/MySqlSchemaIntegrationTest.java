package com.shoestore.shared.persistence.schema;

import com.shoestore.ShoeStoreBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest(
        classes = ShoeStoreBackendApplication.class,
        properties = {
                "spring.flyway.locations="
                        + "classpath:db/migration,"
                        + "classpath:db/schema-convention-migration"
        }
)
@ActiveProfiles("integration-test")
class MySqlSchemaIntegrationTest {

    private static final String TABLE_NAME =
            "mysql_schema_test_records";

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.4.5")
                    .withDatabaseName("shoe_store")
                    .withUsername("shoe_store_app")
                    .withPassword("integration-test-password");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldUseInnoDbUtf8mb4AndApprovedCollation() {
        TableMetadata metadata = jdbcTemplate.queryForObject(
                """
                SELECT ENGINE,
                       TABLE_COLLATION
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                """,
                (resultSet, rowNumber) -> new TableMetadata(
                        resultSet.getString("ENGINE"),
                        resultSet.getString("TABLE_COLLATION")
                ),
                TABLE_NAME
        );

        assertThat(metadata).isNotNull();
        assertThat(metadata.engine()).isEqualTo("InnoDB");
        assertThat(metadata.collation())
                .isEqualTo("utf8mb4_0900_ai_ci");
    }

    @Test
    void shouldDeclareExpectedColumnTypesAndNullability() {
        assertColumn(
                "id",
                "binary",
                "NO",
                16L,
                null,
                null
        );

        assertColumn(
                "version",
                "bigint",
                "NO",
                null,
                19L,
                0L
        );

        assertColumn(
                "record_code",
                "varchar",
                "NO",
                64L,
                null,
                null
        );

        assertColumn(
                "quantity",
                "int",
                "NO",
                null,
                10L,
                0L
        );

        assertColumn(
                "amount",
                "decimal",
                "NO",
                null,
                19L,
                2L
        );

        assertColumn(
                "occurred_at",
                "datetime",
                "NO",
                null,
                null,
                6L
        );

        assertColumn(
                "optional_note",
                "varchar",
                "YES",
                255L,
                null,
                null
        );
    }

    @Test
    void shouldPersistApprovedSchemaValues() {
        UUID id = UUID.randomUUID();
        Instant occurredAt =
                Instant.parse("2026-08-04T10:00:00.123456Z");

        insertRecord(
                id,
                "SCHEMA-001",
                "Giày chạy bộ 👟",
                5,
                new BigDecimal("129.90"),
                true,
                "ACTIVE",
                occurredAt,
                null
        );

        StoredRecord stored = jdbcTemplate.queryForObject(
                """
                SELECT record_code,
                       display_name,
                       quantity,
                       amount,
                       active,
                       status,
                       occurred_at,
                       optional_note
                FROM mysql_schema_test_records
                WHERE id = ?
                """,
                (resultSet, rowNumber) -> new StoredRecord(
                        resultSet.getString("record_code"),
                        resultSet.getString("display_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getBoolean("active"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("occurred_at")
                                .toInstant(),
                        resultSet.getString("optional_note")
                ),
                uuidBytes(id)
        );

        assertThat(stored).isNotNull();
        assertThat(stored.recordCode()).isEqualTo("SCHEMA-001");
        assertThat(stored.displayName())
                .isEqualTo("Giày chạy bộ 👟");
        assertThat(stored.quantity()).isEqualTo(5);
        assertThat(stored.amount())
                .isEqualByComparingTo("129.90");
        assertThat(stored.active()).isTrue();
        assertThat(stored.status()).isEqualTo("ACTIVE");
        assertThat(stored.occurredAt()).isEqualTo(occurredAt);
        assertThat(stored.optionalNote()).isNull();
    }

    @Test
    void shouldEnforceNonNegativeQuantityCheckConstraint() {
        assertCheckConstraintViolation(
                () -> insertRecord(
                        UUID.randomUUID(),
                        "SCHEMA-NEGATIVE-QUANTITY",
                        "Invalid quantity",
                        -1,
                        new BigDecimal("10.00"),
                        true,
                        "ACTIVE",
                        Instant.parse("2026-08-04T10:00:00Z"),
                        null
                ),
                "ck_mysql_schema_test_records__quantity_non_negative"
        );
    }

    @Test
    void shouldEnforceNonNegativeAmountCheckConstraint() {
        assertCheckConstraintViolation(
                () -> insertRecord(
                        UUID.randomUUID(),
                        "SCHEMA-NEGATIVE-AMOUNT",
                        "Invalid amount",
                        1,
                        new BigDecimal("-0.01"),
                        true,
                        "ACTIVE",
                        Instant.parse("2026-08-04T10:00:00Z"),
                        null
                ),
                "ck_mysql_schema_test_records__amount_non_negative"
        );
    }

    @Test
    void shouldEnforceAllowedStatusValues() {
        assertCheckConstraintViolation(
                () -> insertRecord(
                        UUID.randomUUID(),
                        "SCHEMA-INVALID-STATUS",
                        "Invalid status",
                        1,
                        new BigDecimal("10.00"),
                        true,
                        "UNKNOWN",
                        Instant.parse("2026-08-04T10:00:00Z"),
                        null
                ),
                "ck_mysql_schema_test_records__status"
        );
    }

    @Test
    void shouldEnforceUniqueRecordCode() {
        insertRecord(
                UUID.randomUUID(),
                "SCHEMA-UNIQUE",
                "First record",
                1,
                new BigDecimal("10.00"),
                true,
                "ACTIVE",
                Instant.parse("2026-08-04T10:00:00Z"),
                null
        );

        assertThatThrownBy(() -> insertRecord(
                UUID.randomUUID(),
                "SCHEMA-UNIQUE",
                "Second record",
                2,
                new BigDecimal("20.00"),
                false,
                "INACTIVE",
                Instant.parse("2026-08-04T11:00:00Z"),
                null
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldCreateNamedSecondaryIndex() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND INDEX_NAME = ?
                  AND COLUMN_NAME = 'occurred_at'
                """,
                Integer.class,
                TABLE_NAME,
                "ix_mysql_schema_test_records__occurred_at"
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateNamedAndEnforcedCheckConstraints() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.TABLE_CONSTRAINTS
                WHERE CONSTRAINT_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND CONSTRAINT_TYPE = 'CHECK'
                  AND CONSTRAINT_NAME IN (
                      'ck_mysql_schema_test_records__quantity_non_negative',
                      'ck_mysql_schema_test_records__amount_non_negative',
                      'ck_mysql_schema_test_records__status'
                  )
                """,
                Integer.class,
                TABLE_NAME
        );

        assertThat(count).isEqualTo(3);
    }

    private void assertColumn(
            String columnName,
            String expectedDataType,
            String expectedNullable,
            Long expectedCharacterLength,
            Long expectedNumericPrecision,
            Long expectedScale
    ) {
        ColumnMetadata column = jdbcTemplate.queryForObject(
                """
                SELECT DATA_TYPE,
                       IS_NULLABLE,
                       CHARACTER_MAXIMUM_LENGTH,
                       NUMERIC_PRECISION,
                       COALESCE(
                           DATETIME_PRECISION,
                           NUMERIC_SCALE
                       ) AS TYPE_SCALE
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """,
                (resultSet, rowNumber) -> new ColumnMetadata(
                        resultSet.getString("DATA_TYPE")
                                .toLowerCase(Locale.ROOT),
                        resultSet.getString("IS_NULLABLE"),
                        nullableLong(
                                resultSet,
                                "CHARACTER_MAXIMUM_LENGTH"
                        ),
                        nullableLong(
                                resultSet,
                                "NUMERIC_PRECISION"
                        ),
                        nullableLong(resultSet, "TYPE_SCALE")
                ),
                TABLE_NAME,
                columnName
        );

        assertThat(column).isNotNull();
        assertThat(column.dataType())
                .isEqualTo(expectedDataType);
        assertThat(column.nullable())
                .isEqualTo(expectedNullable);
        assertThat(column.characterLength())
                .isEqualTo(expectedCharacterLength);
        assertThat(column.numericPrecision())
                .isEqualTo(expectedNumericPrecision);
        assertThat(column.scale())
                .isEqualTo(expectedScale);
    }

    private void insertRecord(
            UUID id,
            String recordCode,
            String displayName,
            int quantity,
            BigDecimal amount,
            boolean active,
            String status,
            Instant occurredAt,
            String optionalNote
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO mysql_schema_test_records
                (
                    id,
                    version,
                    record_code,
                    display_name,
                    quantity,
                    amount,
                    active,
                    status,
                    occurred_at,
                    optional_note
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                uuidBytes(id),
                0L,
                recordCode,
                displayName,
                quantity,
                amount,
                active,
                status,
                Timestamp.from(occurredAt),
                optionalNote
        );
    }

    private static byte[] uuidBytes(UUID value) {
        byte[] bytes = new byte[16];

        java.nio.ByteBuffer.wrap(bytes)
                .putLong(value.getMostSignificantBits())
                .putLong(value.getLeastSignificantBits());

        return bytes;
    }

    private static Long nullableLong(
            java.sql.ResultSet resultSet,
            String columnName
    ) throws java.sql.SQLException {
        long value = resultSet.getLong(columnName);

        return resultSet.wasNull() ? null : value;
    }

    private static void assertCheckConstraintViolation(
            Runnable operation,
            String constraintName
    ) {
        assertThatThrownBy(operation::run)
                .isInstanceOf(DataAccessException.class)
                .hasRootCauseInstanceOf(SQLException.class)
                .satisfies(throwable -> {
                    Throwable rootCause = throwable.getCause();

                    while (rootCause != null
                            && !(rootCause instanceof SQLException)) {
                        rootCause = rootCause.getCause();
                    }

                    assertThat(rootCause)
                            .isInstanceOf(SQLException.class);

                    SQLException sqlException = (SQLException) rootCause;

                    assertThat(sqlException.getErrorCode())
                            .isEqualTo(3819);

                    assertThat(sqlException.getMessage())
                            .contains(constraintName)
                            .containsIgnoringCase("violated");
                });
    }

    private record TableMetadata(
            String engine,
            String collation
    ) {
    }

    private record ColumnMetadata(
            String dataType,
            String nullable,
            Long characterLength,
            Long numericPrecision,
            Long scale
    ) {
    }

    private record StoredRecord(
            String recordCode,
            String displayName,
            int quantity,
            BigDecimal amount,
            boolean active,
            String status,
            Instant occurredAt,
            String optionalNote
    ) {
    }
}
