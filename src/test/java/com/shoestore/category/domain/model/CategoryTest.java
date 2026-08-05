package com.shoestore.category.domain.model;

import com.shoestore.shared.domain.model.AggregateRoot;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CategoryTest {

    @Test
    void shouldCreateCategory() {
        CategoryId id = CategoryId.generate();
        CategoryName name = CategoryName.of("Running Shoes");
        CategoryDescription description =
                CategoryDescription.of(
                        "Shoes designed for running."
                );

        Category category = Category.create(
                id,
                name,
                description
        );

        assertThat(category.id()).isEqualTo(id);
        assertThat(category.name()).isEqualTo(name);
        assertThat(category.description())
                .isEqualTo(description);
    }

    @Test
    void shouldCreateCategoryWithEmptyDescription() {
        Category category = Category.create(
                CategoryId.generate(),
                CategoryName.of("Running Shoes")
        );

        assertThat(category.description())
                .isEqualTo(CategoryDescription.empty());
        assertThat(category.description().isEmpty()).isTrue();
    }

    @Test
    void shouldRenameCategory() {
        Category category = createCategory();

        CategoryName newName =
                CategoryName.of("Performance Running Shoes");

        category.rename(newName);

        assertThat(category.name()).isEqualTo(newName);
    }

    @Test
    void shouldChangeCategoryDescription() {
        Category category = createCategory();

        CategoryDescription newDescription =
                CategoryDescription.of(
                        "Performance shoes for long-distance running."
                );

        category.changeDescription(newDescription);

        assertThat(category.description())
                .isEqualTo(newDescription);
    }

    @Test
    void shouldAllowClearingCategoryDescription() {
        Category category = createCategory();

        category.changeDescription(
                CategoryDescription.empty()
        );

        assertThat(category.description().isEmpty()).isTrue();
    }

    @Test
    void shouldRejectNullNameWhenCreatingCategory() {
        assertThatNullPointerException()
                .isThrownBy(() -> Category.create(
                        CategoryId.generate(),
                        null,
                        CategoryDescription.empty()
                ))
                .withMessage("Category name must not be null");
    }

    @Test
    void shouldRejectNullDescriptionWhenCreatingCategory() {
        assertThatNullPointerException()
                .isThrownBy(() -> Category.create(
                        CategoryId.generate(),
                        CategoryName.of("Running Shoes"),
                        null
                ))
                .withMessage(
                        "Category description must not be null"
                );
    }

    @Test
    void shouldRejectNullNameWhenRenamingCategory() {
        Category category = createCategory();

        assertThatNullPointerException()
                .isThrownBy(() -> category.rename(null))
                .withMessage("Category name must not be null");
    }

    @Test
    void shouldPreserveNameWhenRenameFails() {
        Category category = createCategory();
        CategoryName originalName = category.name();

        assertThatNullPointerException()
                .isThrownBy(() -> category.rename(null));

        assertThat(category.name()).isEqualTo(originalName);
    }

    @Test
    void shouldRejectNullDescriptionWhenChangingDescription() {
        Category category = createCategory();

        assertThatNullPointerException()
                .isThrownBy(
                        () -> category.changeDescription(null)
                )
                .withMessage(
                        "Category description must not be null"
                );
    }

    @Test
    void shouldPreserveDescriptionWhenChangeFails() {
        Category category = createCategory();
        CategoryDescription originalDescription =
                category.description();

        assertThatNullPointerException()
                .isThrownBy(
                        () -> category.changeDescription(null)
                );

        assertThat(category.description())
                .isEqualTo(originalDescription);
    }

    @Test
    void shouldBeEqualWhenCategoryIdsAreEqual() {
        CategoryId id = CategoryId.generate();

        Category first = Category.create(
                id,
                CategoryName.of("Running Shoes"),
                CategoryDescription.of("First description")
        );

        Category second = Category.create(
                id,
                CategoryName.of("Basketball Shoes"),
                CategoryDescription.of("Second description")
        );

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenCategoryIdsAreDifferent() {
        Category first = createCategory();
        Category second = createCategory();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExtendAggregateRoot() {
        Category category = createCategory();

        assertThat(category)
                .isInstanceOf(AggregateRoot.class);
    }

    private static Category createCategory() {
        return Category.create(
                CategoryId.generate(),
                CategoryName.of("Running Shoes"),
                CategoryDescription.of(
                        "Shoes designed for running."
                )
        );
    }
}
