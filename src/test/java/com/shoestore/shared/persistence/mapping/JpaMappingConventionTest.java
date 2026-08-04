package com.shoestore.shared.persistence.mapping;

import com.shoestoretest.persistence.mapping.MappingTestDetails;
import com.shoestoretest.persistence.mapping.MappingTestEntity;
import com.shoestoretest.persistence.mapping.MappingTestStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class JpaMappingConventionTest {

    @Test
    void shouldDeclareExplicitEntityTableName() {
        Entity entity =
                MappingTestEntity.class.getAnnotation(Entity.class);
        Table table =
                MappingTestEntity.class.getAnnotation(Table.class);

        assertThat(entity).isNotNull();
        assertThat(table).isNotNull();
        assertThat(table.name())
                .isEqualTo("mapping_test_entities");
    }

    @Test
    void shouldMapRequiredStringWithExplicitColumnMetadata()
            throws Exception {
        Field field = MappingTestEntity.class
                .getDeclaredField("displayName");

        Column column = field.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.name()).isEqualTo("display_name");
        assertThat(column.nullable()).isFalse();
        assertThat(column.length()).isEqualTo(120);
    }

    @Test
    void shouldMapOptionalStringWithExplicitColumnMetadata()
            throws Exception {
        Field field = MappingTestEntity.class
                .getDeclaredField("description");

        Column column = field.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.name()).isEqualTo("description");
        assertThat(column.nullable()).isTrue();
        assertThat(column.length()).isEqualTo(500);
    }

    @Test
    void shouldMapEnumAsString() throws Exception {
        Field field = MappingTestEntity.class
                .getDeclaredField("status");

        Enumerated enumerated =
                field.getAnnotation(Enumerated.class);
        Column column =
                field.getAnnotation(Column.class);

        assertThat(field.getType())
                .isEqualTo(MappingTestStatus.class);

        assertThat(enumerated).isNotNull();
        assertThat(enumerated.value())
                .isEqualTo(EnumType.STRING);

        assertThat(column).isNotNull();
        assertThat(column.name()).isEqualTo("status");
        assertThat(column.nullable()).isFalse();
        assertThat(column.length()).isEqualTo(32);
    }

    @Test
    void shouldDeclareEmbeddableDetails() {
        assertThat(
                MappingTestDetails.class.isAnnotationPresent(
                        Embeddable.class
                )
        ).isTrue();
    }

    @Test
    void shouldEmbedDetailsUsingExplicitOverrides()
            throws Exception {
        Field field = MappingTestEntity.class
                .getDeclaredField("details");

        Embedded embedded = field.getAnnotation(Embedded.class);
        AttributeOverrides overrides =
                field.getAnnotation(AttributeOverrides.class);

        assertThat(embedded).isNotNull();
        assertThat(overrides).isNotNull();
        assertThat(overrides.value()).hasSize(2);

        AttributeOverride externalReferenceOverride =
                findOverride(
                        overrides,
                        "externalReference"
                );

        assertThat(
                externalReferenceOverride.column().name()
        ).isEqualTo("external_reference");

        assertThat(
                externalReferenceOverride.column().nullable()
        ).isFalse();

        assertThat(
                externalReferenceOverride.column().length()
        ).isEqualTo(64);

        AttributeOverride noteOverride =
                findOverride(overrides, "note");

        assertThat(noteOverride.column().name())
                .isEqualTo("details_note");
        assertThat(noteOverride.column().nullable())
                .isTrue();
        assertThat(noteOverride.column().length())
                .isEqualTo(255);
    }

    @Test
    void shouldNotUseColumnDefinitionInFixtureMapping() {
        for (Field field : MappingTestEntity.class
                .getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);

            if (column != null) {
                assertThat(column.columnDefinition()).isBlank();
            }
        }
    }

    private static AttributeOverride findOverride(
            AttributeOverrides overrides,
            String attributeName
    ) {
        return java.util.Arrays.stream(overrides.value())
                .filter(override ->
                        override.name().equals(attributeName)
                )
                .findFirst()
                .orElseThrow();
    }
}
