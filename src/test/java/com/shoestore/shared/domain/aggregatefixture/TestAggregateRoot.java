package com.shoestore.shared.domain.aggregatefixture;

import com.shoestore.shared.domain.model.AggregateRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class TestAggregateRoot
        extends AggregateRoot<TestAggregateId> {

    private static final int MAX_TOTAL_QUANTITY = 100;

    private final List<TestAggregateItem> items =
            new ArrayList<>();

    TestAggregateRoot(TestAggregateId id) {
        super(id);
    }

    List<TestAggregateItem> items() {
        return List.copyOf(items);
    }

    int totalQuantity() {
        return items.stream()
                .mapToInt(TestAggregateItem::quantity)
                .sum();
    }

    void addItem(
            TestChildId itemId,
            int quantity
    ) {
        Objects.requireNonNull(
                itemId,
                "Item id must not be null"
        );

        requirePositiveQuantity(quantity);
        requireCapacity(quantity);

        Optional<TestAggregateItem> existing =
                findItem(itemId);

        if (existing.isPresent()) {
            existing.get().increaseBy(quantity);
            return;
        }

        items.add(new TestAggregateItem(itemId, quantity));
    }

    void removeItem(TestChildId itemId) {
        Objects.requireNonNull(
                itemId,
                "Item id must not be null"
        );

        boolean removed = items.removeIf(
                item -> item.id().equals(itemId)
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Item does not belong to aggregate"
            );
        }
    }

    private Optional<TestAggregateItem> findItem(
            TestChildId itemId
    ) {
        return items.stream()
                .filter(item -> item.id().equals(itemId))
                .findFirst();
    }

    private void requireCapacity(int additionalQuantity) {
        int resultingQuantity = Math.addExact(
                totalQuantity(),
                additionalQuantity
        );

        if (resultingQuantity > MAX_TOTAL_QUANTITY) {
            throw new IllegalArgumentException(
                    "Aggregate total quantity must not exceed 100"
            );
        }
    }

    private static void requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }
    }
}
