package com.shoestore.product.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ProductStatusTest {

    @Test
    void shouldDefineInactiveStatus() {
        assertThat(ProductStatus.INACTIVE.name())
                .isEqualTo("INACTIVE");
    }

    @Test
    void shouldDefineActiveStatus() {
        assertThat(ProductStatus.ACTIVE.name())
                .isEqualTo("ACTIVE");
    }

    @Test
    void shouldContainOnlySupportedLifecycleStatuses() {
        assertThat(ProductStatus.values())
                .containsExactly(
                        ProductStatus.INACTIVE,
                        ProductStatus.ACTIVE
                );
    }

    @Test
    void shouldResolveStatusFromStableName() {
        assertThat(ProductStatus.valueOf("INACTIVE"))
                .isEqualTo(ProductStatus.INACTIVE);

        assertThat(ProductStatus.valueOf("ACTIVE"))
                .isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void shouldNotContainInventoryStatus() {
        assertThat(
                Arrays.stream(ProductStatus.values())
                        .map(Enum::name)
        ).doesNotContain(
                "OUT_OF_STOCK",
                "IN_STOCK",
                "LOW_STOCK"
        );
    }
}
