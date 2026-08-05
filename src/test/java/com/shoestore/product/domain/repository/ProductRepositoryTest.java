package com.shoestore.product.domain.repository;

import com.shoestore.product.domain.model.Product;
import com.shoestore.product.domain.model.ProductId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest {

    @Test
    void shouldBeAnInterface() {
        assertThat(ProductRepository.class.isInterface()).isTrue();
    }

    @Test
    void shouldDeclareSaveOperationForProductAggregate()
            throws NoSuchMethodException {

        Method method = ProductRepository.class.getDeclaredMethod(
                "save",
                Product.class
        );

        assertThat(method.getReturnType()).isEqualTo(void.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldDeclareFindByIdUsingStrongProductIdentity()
            throws NoSuchMethodException {

        Method method = ProductRepository.class.getDeclaredMethod(
                "findById",
                ProductId.class
        );

        assertThat(method.getReturnType()).isEqualTo(Optional.class);
        assertThat(Modifier.isAbstract(method.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
    }

    @Test
    void shouldReturnOptionalOfProductFromFindById()
            throws NoSuchMethodException {

        Method method = ProductRepository.class.getDeclaredMethod(
                "findById",
                ProductId.class
        );

        Type genericReturnType = method.getGenericReturnType();

        assertThat(genericReturnType)
                .isInstanceOf(ParameterizedType.class);

        ParameterizedType parameterizedType =
                (ParameterizedType) genericReturnType;

        assertThat(parameterizedType.getRawType())
                .isEqualTo(Optional.class);

        assertThat(parameterizedType.getActualTypeArguments())
                .containsExactly(Product.class);
    }

    @Test
    void shouldExposeOnlyMinimalAggregateOperations() {
        Method[] declaredMethods =
                ProductRepository.class.getDeclaredMethods();

        assertThat(declaredMethods)
                .extracting(Method::getName)
                .containsExactlyInAnyOrder(
                        "save",
                        "findById"
                );
    }

    @Test
    void shouldNotExtendAnotherRepositoryAbstraction() {
        assertThat(ProductRepository.class.getInterfaces())
                .isEmpty();
    }
}
