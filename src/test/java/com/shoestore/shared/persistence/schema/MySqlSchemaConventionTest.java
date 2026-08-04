package com.shoestore.shared.persistence.schema;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class MySqlSchemaConventionTest {

    private static final Path MIGRATION_PATH = Path.of(
            "src",
            "test",
            "resources",
            "db",
            "schema-convention-migration",
            "V1003__create_mysql_schema_convention_fixture.sql"
    );

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(
            "\\b(?:TABLE|CONSTRAINT|INDEX)\\s+"
                    + "(?:IF\\s+NOT\\s+EXISTS\\s+)?"
                    + "([a-zA-Z0-9_]+)",
            Pattern.CASE_INSENSITIVE
    );

    @Test
    void shouldUseInnoDbUtf8mb4AndApprovedCollation()
            throws IOException {
        String migration = readMigration();

        assertThat(migration)
                .containsIgnoringCase("ENGINE = InnoDB")
                .containsIgnoringCase(
                        "DEFAULT CHARACTER SET = utf8mb4"
                )
                .containsIgnoringCase(
                        "COLLATE = utf8mb4_0900_ai_ci"
                );
    }

    @Test
    void shouldUseApprovedTechnicalColumnTypes()
            throws IOException {
        String migration = normalizedMigration();

        assertThat(migration)
                .contains("id binary(16) not null")
                .contains("version bigint not null")
                .contains("amount decimal(19, 2) not null")
                .contains("occurred_at datetime(6) not null");
    }

    @Test
    void shouldDeclareExplicitNullability()
            throws IOException {
        String migration = normalizedMigration();

        assertThat(migration)
                .contains("record_code varchar(64) not null")
                .contains("display_name varchar(120) not null")
                .contains("optional_note varchar(255) null");
    }

    @Test
    void shouldDeclareNamedConstraintsAndIndex()
            throws IOException {
        String migration = readMigration();

        assertThat(migration)
                .contains(
                        "CONSTRAINT pk_mysql_schema_test_records"
                )
                .contains(
                        "CONSTRAINT "
                                + "uk_mysql_schema_test_records"
                                + "__record_code"
                )
                .contains(
                        "CONSTRAINT "
                                + "ck_mysql_schema_test_records"
                                + "__quantity_non_negative"
                )
                .contains(
                        "CONSTRAINT "
                                + "ck_mysql_schema_test_records"
                                + "__amount_non_negative"
                )
                .contains(
                        "CONSTRAINT "
                                + "ck_mysql_schema_test_records"
                                + "__status"
                )
                .contains(
                        "INDEX "
                                + "ix_mysql_schema_test_records"
                                + "__occurred_at"
                );
    }

    @Test
    void shouldUseLowercaseSnakeCaseIdentifiers()
            throws IOException {
        Matcher matcher = IDENTIFIER_PATTERN.matcher(
                readMigration()
        );

        while (matcher.find()) {
            String identifier = matcher.group(1);

            assertThat(identifier)
                    .matches("[a-z][a-z0-9_]*");
        }
    }

    @Test
    void shouldKeepIdentifiersWithinMySqlLimit()
            throws IOException {
        Matcher matcher = IDENTIFIER_PATTERN.matcher(
                readMigration()
        );

        while (matcher.find()) {
            String identifier = matcher.group(1);

            assertThat(identifier.length())
                    .as("Identifier: %s", identifier)
                    .isLessThanOrEqualTo(64);
        }
    }

    @Test
    void shouldNotUseDisallowedSchemaFeatures()
            throws IOException {
        String migration = normalizedMigration();

        assertThat(migration)
                .doesNotContain(" enum(")
                .doesNotContain(" float")
                .doesNotContain(" double")
                .doesNotContain(" unsigned")
                .doesNotContain("uuid()")
                .doesNotContain("current_timestamp")
                .doesNotContain("on update current_timestamp");
    }

    @Test
    void shouldUseExplicitCheckConstraints()
            throws IOException {
        String migration = normalizedMigration();

        assertThat(migration)
                .contains("check (quantity >= 0)")
                .contains("check (amount >= 0)")
                .contains(
                        "check (status in ('active', 'inactive'))"
                );
    }

    private static String readMigration() throws IOException {
        assertThat(MIGRATION_PATH).exists();

        return Files.readString(
                MIGRATION_PATH,
                StandardCharsets.UTF_8
        );
    }

    private static String normalizedMigration()
            throws IOException {
        return readMigration()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }
}
