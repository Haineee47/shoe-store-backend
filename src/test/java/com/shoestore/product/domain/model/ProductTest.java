package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.AggregateRoot;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ProductTest {

    @Test
    void shouldCreateInactiveProduct() {
        ProductId id = ProductId.generate();
        ProductName name = ProductName.of("Nike Air Max");
        ProductDescription description =
                ProductDescription.of(
                        "Lightweight running shoes."
                );
        ProductSku sku = ProductSku.of("NIKE-AIR-MAX");

        Product product = Product.create(
                id,
                name,
                description,
                sku
        );

        assertThat(product.id()).isEqualTo(id);
        assertThat(product.name()).isEqualTo(name);
        assertThat(product.description())
                .isEqualTo(description);
        assertThat(product.sku()).isEqualTo(sku);
        assertThat(product.status())
                .isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.isInactive()).isTrue();
        assertThat(product.isActive()).isFalse();
    }

    @Test
    void shouldCreateProductWithEmptyDescription() {
        Product product = Product.create(
                ProductId.generate(),
                ProductName.of("Nike Air Max"),
                ProductSku.of("NIKE-AIR-MAX")
        );

        assertThat(product.description())
                .isEqualTo(ProductDescription.empty());
        assertThat(product.description().isEmpty()).isTrue();
    }

    @Test
    void shouldRenameProduct() {
        Product product = createProduct();

        ProductName newName =
                ProductName.of("Nike Air Max Premium");

        product.rename(newName);

        assertThat(product.name()).isEqualTo(newName);
    }

    @Test
    void shouldChangeProductDescription() {
        Product product = createProduct();

        ProductDescription newDescription =
                ProductDescription.of(
                        "Premium lightweight running shoes."
                );

        product.changeDescription(newDescription);

        assertThat(product.description())
                .isEqualTo(newDescription);
    }

    @Test
    void shouldAllowClearingProductDescription() {
        Product product = createProduct();

        product.changeDescription(
                ProductDescription.empty()
        );

        assertThat(product.description().isEmpty()).isTrue();
    }

    @Test
    void shouldActivateInactiveProduct() {
        Product product = createProduct();

        product.activate();

        assertThat(product.status())
                .isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.isActive()).isTrue();
        assertThat(product.isInactive()).isFalse();
    }

    @Test
    void shouldActivateIdempotently() {
        Product product = createProduct();

        product.activate();
        product.activate();

        assertThat(product.status())
                .isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void shouldDeactivateActiveProduct() {
        Product product = createProduct();
        product.activate();

        product.deactivate();

        assertThat(product.status())
                .isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.isInactive()).isTrue();
        assertThat(product.isActive()).isFalse();
    }

    @Test
    void shouldDeactivateIdempotently() {
        Product product = createProduct();

        product.deactivate();
        product.deactivate();

        assertThat(product.status())
                .isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    void shouldRejectNullNameWhenCreatingProduct() {
        assertThatNullPointerException()
                .isThrownBy(() -> Product.create(
                        ProductId.generate(),
                        null,
                        ProductDescription.empty(),
                        ProductSku.of("NIKE-AIR-MAX")
                ))
                .withMessage("Product name must not be null");
    }

    @Test
    void shouldRejectNullDescriptionWhenCreatingProduct() {
        assertThatNullPointerException()
                .isThrownBy(() -> Product.create(
                        ProductId.generate(),
                        ProductName.of("Nike Air Max"),
                        null,
                        ProductSku.of("NIKE-AIR-MAX")
                ))
                .withMessage(
                        "Product description must not be null"
                );
    }

    @Test
    void shouldRejectNullSkuWhenCreatingProduct() {
        assertThatNullPointerException()
                .isThrownBy(() -> Product.create(
                        ProductId.generate(),
                        ProductName.of("Nike Air Max"),
                        ProductDescription.empty(),
                        null
                ))
                .withMessage("Product SKU must not be null");
    }

    @Test
    void shouldRejectNullNameWhenRenamingProduct() {
        Product product = createProduct();

        assertThatNullPointerException()
                .isThrownBy(() -> product.rename(null))
                .withMessage("Product name must not be null");
    }

    @Test
    void shouldPreserveNameWhenRenameFails() {
        Product product = createProduct();
        ProductName originalName = product.name();

        assertThatNullPointerException()
                .isThrownBy(() -> product.rename(null));

        assertThat(product.name()).isEqualTo(originalName);
    }

    @Test
    void shouldRejectNullDescriptionWhenChangingDescription() {
        Product product = createProduct();

        assertThatNullPointerException()
                .isThrownBy(
                        () -> product.changeDescription(null)
                )
                .withMessage(
                        "Product description must not be null"
                );
    }

    @Test
    void shouldPreserveDescriptionWhenChangeFails() {
        Product product = createProduct();
        ProductDescription originalDescription =
                product.description();

        assertThatNullPointerException()
                .isThrownBy(
                        () -> product.changeDescription(null)
                );

        assertThat(product.description())
                .isEqualTo(originalDescription);
    }

    @Test
    void shouldBeEqualWhenProductIdsAreEqual() {
        ProductId id = ProductId.generate();

        Product first = Product.create(
                id,
                ProductName.of("Nike Air Max"),
                ProductDescription.of("First description"),
                ProductSku.of("NIKE-AIR-MAX")
        );

        Product second = Product.create(
                id,
                ProductName.of("Adidas Ultraboost"),
                ProductDescription.of("Second description"),
                ProductSku.of("ADIDAS-ULTRABOOST")
        );

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenProductIdsAreDifferent() {
        Product first = createProduct();
        Product second = createProduct();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExtendAggregateRoot() {
        Product product = createProduct();

        assertThat(product)
                .isInstanceOf(AggregateRoot.class);
    }

    @Test
    void shouldRejectNullIdentityWhenCreatingProduct() {
        assertThatNullPointerException()
                .isThrownBy(() -> Product.create(
                        null,
                        ProductName.of("Nike Air Max"),
                        ProductDescription.empty(),
                        ProductSku.of("NIKE-AIR-MAX")
                ))
                .withMessage(
                        "Domain entity id must not be null"
                );
    }

    private static Product createProduct() {
        return Product.create(
                ProductId.generate(),
                ProductName.of("Nike Air Max"),
                ProductDescription.of(
                        "Lightweight running shoes."
                ),
                ProductSku.of("NIKE-AIR-MAX")
        );
    }
}
