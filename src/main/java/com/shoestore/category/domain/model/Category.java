package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.AggregateRoot;

import java.util.Objects;

/**
 * Aggregate root representing a product category.
 *
 * <p>The category root owns its name and description. All category state
 * changes must be performed through the behavior exposed by this class.</p>
 */
public final class Category extends AggregateRoot<CategoryId> {

    private CategoryName name;
    private CategoryDescription description;

    private Category(
            CategoryId id,
            CategoryName name,
            CategoryDescription description
    ) {
        super(id);
        this.name = requireName(name);
        this.description = requireDescription(description);
    }

    /**
     * Creates a category with an explicit identity.
     *
     * <p>Identity generation remains explicit so callers and tests can control
     * the aggregate identity deterministically when required.</p>
     */
    public static Category create(
            CategoryId id,
            CategoryName name,
            CategoryDescription description
    ) {
        return new Category(id, name, description);
    }

    /**
     * Creates a category with an empty description.
     */
    public static Category create(
            CategoryId id,
            CategoryName name
    ) {
        return new Category(
                id,
                name,
                CategoryDescription.empty()
        );
    }

    public CategoryName name() {
        return name;
    }

    public CategoryDescription description() {
        return description;
    }

    public void rename(CategoryName newName) {
        this.name = requireName(newName);
    }

    public void changeDescription(
            CategoryDescription newDescription
    ) {
        this.description = requireDescription(newDescription);
    }

    private static CategoryName requireName(CategoryName name) {
        return Objects.requireNonNull(
                name,
                "Category name must not be null"
        );
    }

    private static CategoryDescription requireDescription(
            CategoryDescription description
    ) {
        return Objects.requireNonNull(
                description,
                "Category description must not be null"
        );
    }
}
