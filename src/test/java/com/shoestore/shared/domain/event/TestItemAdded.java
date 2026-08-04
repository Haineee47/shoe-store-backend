package com.shoestore.shared.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

record TestItemAdded(
        UUID aggregateId,
        UUID itemId,
        int quantity,
        Instant occurredAt
) implements DomainEvent {

    TestItemAdded {
        Objects.requireNonNull(
                aggregateId,
                "Aggregate id must not be null"
        );

        Objects.requireNonNull(
                itemId,
                "Item id must not be null"
        );

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        Objects.requireNonNull(
                occurredAt,
                "Event occurrence time must not be null"
        );
    }
}
