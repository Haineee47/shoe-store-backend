package com.shoestore.product.domain.model;

/**
 * Business lifecycle status of a product.
 *
 * <p>An inactive product is not currently available for business use.
 * An active product is enabled in the product catalog. This status does
 * not represent inventory availability or stock quantity.</p>
 */
public enum ProductStatus {

    /**
     * Product is not enabled for active catalog use.
     */
    INACTIVE,

    /**
     * Product is enabled for active catalog use.
     */
    ACTIVE
}
