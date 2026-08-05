package com.shoestore.category.domain.repository;

import com.shoestore.category.domain.model.Category;
import com.shoestore.category.domain.model.CategoryId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryTest {

    @Test
    void shouldBeAnInterface() {
        assertThat(CategoryRepository.class.isInterface()).isTrue();
    }

    @Test
    void shouldDeclareSaveOperationForCategoryAggregate()
            throws NoSuchMethodException {

        Method method = CategoryRepository.class.getDeclaredMethod(
                "save",
                Category.class
        );

        assertThat(method.getReturnType()).isEqualTo(void.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldDeclareFindByIdUsingStrongCategoryIdentity()
            throws NoSuchMethodException {

        Method method = CategoryRepository.class.getDeclaredMethod(
                "findById",
                CategoryId.class
        );

        assertThat(method.getReturnType()).isEqualTo(Optional.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldReturnOptionalOfCategoryFromFindById()
            throws NoSuchMethodException {

        Method method = CategoryRepository.class.getDeclaredMethod(
                "findById",
                CategoryId.class
        );

        Type genericReturnType = method.getGenericReturnType();

        assertThat(genericReturnType)
                .isInstanceOf(ParameterizedType.class);

        ParameterizedType parameterizedType =
                (ParameterizedType) genericReturnType;

        assertThat(parameterizedType.getRawType())
                .isEqualTo(Optional.class);

        assertThat(parameterizedType.getActualTypeArguments())
                .containsExactly(Category.class);
    }

    @Test
    void shouldExposeOnlyMinimalAggregateOperations() {
        Method[] declaredMethods =
                CategoryRepository.class.getDeclaredMethods();

        assertThat(declaredMethods)
                .extracting(Method::getName)
                .containsExactlyInAnyOrder(
                        "save",
                        "findById"
                );
    }

    @Test
    void shouldNotExtendAnotherRepositoryAbstraction() {
        assertThat(CategoryRepository.class.getInterfaces())
                .isEmpty();
    }
}
