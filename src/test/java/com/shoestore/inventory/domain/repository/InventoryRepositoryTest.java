package com.shoestore.inventory.domain.repository;

import com.shoestore.inventory.domain.model.Inventory;
import com.shoestore.inventory.domain.model.InventoryId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryRepositoryTest {

    @Test
    void shouldBeAnInterface() {
        assertThat(InventoryRepository.class.isInterface()).isTrue();
    }

    @Test
    void shouldDeclareSaveOperationForInventoryAggregate()
            throws NoSuchMethodException {

        Method method = InventoryRepository.class.getDeclaredMethod(
                "save",
                Inventory.class
        );

        assertThat(method.getReturnType()).isEqualTo(void.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldDeclareFindByIdUsingStrongInventoryIdentity()
            throws NoSuchMethodException {

        Method method = InventoryRepository.class.getDeclaredMethod(
                "findById",
                InventoryId.class
        );

        assertThat(method.getReturnType()).isEqualTo(Optional.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldReturnOptionalOfInventoryFromFindById()
            throws NoSuchMethodException {

        Method method = InventoryRepository.class.getDeclaredMethod(
                "findById",
                InventoryId.class
        );

        Type genericReturnType = method.getGenericReturnType();

        assertThat(genericReturnType)
                .isInstanceOf(ParameterizedType.class);

        ParameterizedType parameterizedType =
                (ParameterizedType) genericReturnType;

        assertThat(parameterizedType.getRawType())
                .isEqualTo(Optional.class);

        assertThat(parameterizedType.getActualTypeArguments())
                .containsExactly(Inventory.class);
    }

    @Test
    void shouldExposeOnlyMinimalAggregateOperations() {
        Method[] declaredMethods =
                InventoryRepository.class.getDeclaredMethods();

        assertThat(declaredMethods)
                .extracting(Method::getName)
                .containsExactlyInAnyOrder(
                        "save",
                        "findById"
                );
    }

    @Test
    void shouldNotExtendAnotherRepositoryAbstraction() {
        assertThat(InventoryRepository.class.getInterfaces())
                .isEmpty();
    }
}
