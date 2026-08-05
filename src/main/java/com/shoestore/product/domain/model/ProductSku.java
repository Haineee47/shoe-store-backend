package com.shoestore.product.domain.model;

import com.shoestore.shared.domain.model.ValueObject;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Catalog-level stock keeping unit of a product.
 *
 * <p>The SKU is normalized to uppercase and may contain only Latin
 * letters, digits, hyphens, and underscores.</p>
 *
 * <p>This SKU currently identifies the catalog-level product. Whether SKU
 * ownership moves to a future product variant remains a deferred decision.</p>
 */
public final class ProductSku implements ValueObject {

    private static final Pattern VALID_PATTERN =
            Pattern.compile("[A-Z0-9_-]+");

    private final String value;

    private ProductSku(String value) {
        this.value = normalize(value);
    }

    public static ProductSku of(String value) {
        return new ProductSku(value);
    }

    public String value() {
        return value;
    }

    private static String normalize(String value) {
        Objects.requireNonNull(
                value,
                "Product SKU must not be null"
        );

        String normalizedValue = value
                .strip()
                .toUpperCase(Locale.ROOT);

        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product SKU must not be blank"
            );
        }

        if (!VALID_PATTERN.matcher(normalizedValue).matches()) {
            throw new IllegalArgumentException(
                    "Product SKU must contain only letters, digits, hyphens, or underscores"
            );
        }

        return normalizedValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ProductSku that)) {
            return false;
        }

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
