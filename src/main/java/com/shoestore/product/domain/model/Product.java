package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.AggregateRoot;

import java.util.Objects;

/**
 * Aggregate root representing a catalog product.
 *
 * <p>The Product aggregate owns its catalog-level name, description, SKU,
 * and lifecycle status. Inventory availability, category association,
 * pricing, and product variants belong outside the current aggregate scope.</p>
 */
public final class Product extends AggregateRoot<ProductId> {

    private ProductName name;
    private ProductDescription description;
    private final ProductSku sku;
    private ProductStatus status;

    private Product(
            ProductId id,
            ProductName name,
            ProductDescription description,
            ProductSku sku
    ) {
        super(id);
        this.name = requireName(name);
        this.description = requireDescription(description);
        this.sku = requireSku(sku);
        this.status = ProductStatus.INACTIVE;
    }

    /**
     * Creates a new inactive product with an explicit identity.
     */
    public static Product create(
            ProductId id,
            ProductName name,
            ProductDescription description,
            ProductSku sku
    ) {
        return new Product(
                id,
                name,
                description,
                sku
        );
    }

    /**
     * Creates a new inactive product with an empty description.
     */
    public static Product create(
            ProductId id,
            ProductName name,
            ProductSku sku
    ) {
        return new Product(
                id,
                name,
                ProductDescription.empty(),
                sku
        );
    }

    public ProductName name() {
        return name;
    }

    public ProductDescription description() {
        return description;
    }

    public ProductSku sku() {
        return sku;
    }

    public ProductStatus status() {
        return status;
    }

    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }

    public boolean isInactive() {
        return status == ProductStatus.INACTIVE;
    }

    public void rename(ProductName newName) {
        this.name = requireName(newName);
    }

    public void changeDescription(
            ProductDescription newDescription
    ) {
        this.description = requireDescription(newDescription);
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    private static ProductName requireName(ProductName name) {
        return Objects.requireNonNull(
                name,
                "Product name must not be null"
        );
    }

    private static ProductDescription requireDescription(
            ProductDescription description
    ) {
        return Objects.requireNonNull(
                description,
                "Product description must not be null"
        );
    }

    private static ProductSku requireSku(ProductSku sku) {
        return Objects.requireNonNull(
                sku,
                "Product SKU must not be null"
        );
    }
}
