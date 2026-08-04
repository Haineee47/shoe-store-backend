package com.shoestore.shared.persistence.fixture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceFixtureConventionTest {

    private static final Path TEST_JAVA_ROOT = Path.of(
            "src",
            "test",
            "java"
    );

    private static final Path TEST_PERSISTENCE_FIXTURE_ROOT = Path.of(
            "src",
            "test",
            "java",
            "com",
            "shoestoretest",
            "persistence"
    );

    private static final Path TEST_DATABASE_RESOURCE_ROOT = Path.of(
            "src",
            "test",
            "resources",
            "db"
    );

    private static final Path DEFAULT_TEST_MIGRATION_LOCATION = Path.of(
            "src",
            "test",
            "resources",
            "db",
            "migration"
    );

    private static final Path PRODUCTION_JAVA_ROOT = Path.of(
            "src",
            "main",
            "java"
    );

    private static final Pattern TEST_MIGRATION_NAME_PATTERN =
            Pattern.compile(
                    "V(\\d+)__([a-z0-9_]+)\\.sql"
            );

    @Test
    void persistenceTestEntitiesShouldLiveOutsideApplicationRootPackage()
            throws IOException {
        List<Path> entityFixtures = findFiles(
                TEST_JAVA_ROOT,
                path -> path.getFileName()
                        .toString()
                        .endsWith("TestEntity.java")
        );

        assertThat(entityFixtures)
                .as("Persistence test entity fixtures")
                .isNotEmpty();

        for (Path fixture : entityFixtures) {
            assertThat(fixture.normalize())
                    .as("Fixture path: %s", fixture)
                    .startsWith(
                            TEST_PERSISTENCE_FIXTURE_ROOT.normalize()
                    );
        }
    }

    @Test
    void persistenceTestEntitiesShouldDeclareIsolatedPackage()
            throws IOException {
        List<Path> entityFixtures = findFiles(
                TEST_JAVA_ROOT,
                path -> path.getFileName()
                        .toString()
                        .endsWith("TestEntity.java")
        );

        for (Path fixture : entityFixtures) {
            String source = Files.readString(
                    fixture,
                    StandardCharsets.UTF_8
            );

            assertThat(source)
                    .as("Package declaration in %s", fixture)
                    .containsPattern(
                            "package\\s+"
                                    + "com\\.shoestoretest"
                                    + "\\.persistence(?:\\.[a-z0-9_]+)+;"
                    )
                    .doesNotContain(
                            "package com.shoestore.persistence"
                    )
                    .doesNotContain(
                            "package com.shoestore.shared.persistence"
                    );
        }
    }

    @Test
    void productionSourceShouldNotContainTestPersistenceFixtures()
            throws IOException {
        List<Path> productionFixtures = findFiles(
                PRODUCTION_JAVA_ROOT,
                path -> {
                    String filename = path.getFileName().toString();

                    return filename.endsWith("TestEntity.java")
                            || filename.endsWith(
                                    "TestJpaRepository.java"
                            )
                            || filename.contains("Fixture");
                }
        );

        assertThat(productionFixtures)
                .as("Persistence fixtures in production source")
                .isEmpty();
    }

    @Test
    void defaultTestMigrationLocationShouldRemainEmpty()
            throws IOException {
        if (!Files.exists(DEFAULT_TEST_MIGRATION_LOCATION)) {
            return;
        }

        List<Path> migrationFiles = findFiles(
                DEFAULT_TEST_MIGRATION_LOCATION,
                path -> path.getFileName()
                        .toString()
                        .endsWith(".sql")
        );

        assertThat(migrationFiles)
                .as(
                        "Test migrations must not leak into "
                                + "classpath:db/migration"
                )
                .isEmpty();
    }

    @Test
    void fixtureMigrationsShouldUseIsolatedLocations()
            throws IOException {
        List<Path> fixtureMigrations = findFixtureMigrations();

        assertThat(fixtureMigrations)
                .as("Test-only Flyway migrations")
                .isNotEmpty();

        for (Path migration : fixtureMigrations) {
            Path parent = migration.getParent();

            assertThat(parent)
                    .as("Migration parent: %s", migration)
                    .isNotNull();

            assertThat(parent.getFileName().toString())
                    .as("Migration location: %s", parent)
                    .endsWith("-migration")
                    .isNotEqualTo("migration");
        }
    }

    @Test
    void fixtureMigrationsShouldUseApprovedNamingAndVersionRange()
            throws IOException {
        List<Path> fixtureMigrations = findFixtureMigrations();

        for (Path migration : fixtureMigrations) {
            String filename = migration.getFileName().toString();
            Matcher matcher =
                    TEST_MIGRATION_NAME_PATTERN.matcher(filename);

            assertThat(matcher.matches())
                    .as("Migration filename: %s", filename)
                    .isTrue();

            long version = Long.parseLong(matcher.group(1));

            assertThat(version)
                    .as("Test migration version: %s", filename)
                    .isGreaterThanOrEqualTo(1000L);
        }
    }

    @Test
    void fixtureMigrationVersionsShouldBeUnique()
            throws IOException {
        List<Path> fixtureMigrations = findFixtureMigrations();
        Set<Long> versions = new HashSet<>();

        for (Path migration : fixtureMigrations) {
            Matcher matcher = TEST_MIGRATION_NAME_PATTERN.matcher(
                    migration.getFileName().toString()
            );

            assertThat(matcher.matches()).isTrue();

            long version = Long.parseLong(matcher.group(1));

            assertThat(versions.add(version))
                    .as(
                            "Duplicate test migration version V%s in %s",
                            version,
                            migration
                    )
                    .isTrue();
        }
    }

    @Test
    void eachFixtureMigrationLocationShouldContainSqlMigration()
            throws IOException {
        List<Path> fixtureDirectories;

        try (Stream<Path> paths =
                     Files.list(TEST_DATABASE_RESOURCE_ROOT)) {
            fixtureDirectories = paths
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName()
                            .toString()
                            .endsWith("-migration"))
                    .toList();
        }

        assertThat(fixtureDirectories).isNotEmpty();

        for (Path directory : fixtureDirectories) {
            List<Path> migrations = findFiles(
                    directory,
                    path -> path.getFileName()
                            .toString()
                            .endsWith(".sql")
            );

            assertThat(migrations)
                    .as("Migrations in %s", directory)
                    .isNotEmpty();
        }
    }

    private static List<Path> findFixtureMigrations()
            throws IOException {
        return findFiles(
                TEST_DATABASE_RESOURCE_ROOT,
                path -> {
                    String filename = path.getFileName().toString();

                    if (!filename.endsWith(".sql")) {
                        return false;
                    }

                    Matcher matcher =
                            TEST_MIGRATION_NAME_PATTERN.matcher(
                                    filename
                            );

                    if (!matcher.matches()) {
                        return true;
                    }

                    return Long.parseLong(matcher.group(1)) >= 1000L;
                }
        );
    }

    private static List<Path> findFiles(
            Path root,
            java.util.function.Predicate<Path> predicate
    ) throws IOException {
        if (!Files.exists(root)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(predicate)
                    .sorted()
                    .toList();
        }
    }
}
