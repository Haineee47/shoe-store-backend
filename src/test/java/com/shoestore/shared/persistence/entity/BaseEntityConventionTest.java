package com.shoestore.shared.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityConventionTest {

    @Test
    void shouldBeMappedSuperclass() {
        assertThat(
                BaseEntity.class.isAnnotationPresent(MappedSuperclass.class)
        ).isTrue();
    }

    @Test
    void shouldBeAbstract() {
        assertThat(
                Modifier.isAbstract(BaseEntity.class.getModifiers())
        ).isTrue();
    }

    @Test
    void shouldDeclareUuidIdentifier() throws Exception {
        Field idField = BaseEntity.class.getDeclaredField("id");

        assertThat(idField.getType()).isEqualTo(UUID.class);

        assertThat(
                idField.isAnnotationPresent(Id.class)
        ).isTrue();

        Column column = idField.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.name()).isEqualTo("id");
        assertThat(column.nullable()).isFalse();
        assertThat(column.updatable()).isFalse();
    }

    @Test
    void shouldDeclareNullableWrapperVersionForOptimisticLocking()
            throws Exception {
        Field versionField = BaseEntity.class.getDeclaredField("version");

        assertThat(versionField.getType()).isEqualTo(Long.class);

        assertThat(
                versionField.isAnnotationPresent(Version.class)
        ).isTrue();

        Column column = versionField.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.name()).isEqualTo("version");
        assertThat(column.nullable()).isFalse();
    }

    @Test
    void shouldExposeOnlyProtectedConstructor() {
        Constructor<?>[] constructors =
                BaseEntity.class.getDeclaredConstructors();

        assertThat(constructors).hasSize(1);

        Constructor<?> constructor = constructors[0];

        assertThat(
                Modifier.isProtected(constructor.getModifiers())
        ).isTrue();

        assertThat(constructor.getParameterCount()).isZero();
    }

    @Test
    void shouldNotExposeIdentifierOrVersionSetter() {
        assertThat(BaseEntity.class.getDeclaredMethods())
                .extracting(Method::getName)
                .doesNotContain("setId", "setVersion");
    }

    @Test
    void shouldMakeEqualityMethodsFinal() throws Exception {
        Method equalsMethod = BaseEntity.class.getDeclaredMethod(
                "equals",
                Object.class
        );

        Method hashCodeMethod = BaseEntity.class.getDeclaredMethod(
                "hashCode"
        );

        assertThat(
                Modifier.isFinal(equalsMethod.getModifiers())
        ).isTrue();

        assertThat(
                Modifier.isFinal(hashCodeMethod.getModifiers())
        ).isTrue();
    }
}
