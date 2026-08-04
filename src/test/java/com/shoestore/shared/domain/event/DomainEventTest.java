package com.shoestore.shared.domain.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class DomainEventTest {

    private static final Instant OCCURRED_AT =
            Instant.parse("2026-08-05T00:00:00Z");

    @Test
    void shouldBeRecognizedAsDomainEvent() {
        TestItemAdded event = new TestItemAdded(
                UUID.randomUUID(),
                UUID.randomUUID(),
                2,
                OCCURRED_AT
        );

        assertThat(event)
                .isInstanceOf(DomainEvent.class);
    }

    @Test
    void shouldPreserveOccurrenceTime() {
        TestItemAdded event = new TestItemAdded(
                UUID.randomUUID(),
                UUID.randomUUID(),
                2,
                OCCURRED_AT
        );

        assertThat(event.occurredAt())
                .isEqualTo(OCCURRED_AT);
    }

    @Test
    void shouldPreserveBusinessPayload() {
        UUID aggregateId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        TestItemAdded event = new TestItemAdded(
                aggregateId,
                itemId,
                3,
                OCCURRED_AT
        );

        assertThat(event.aggregateId()).isEqualTo(aggregateId);
        assertThat(event.itemId()).isEqualTo(itemId);
        assertThat(event.quantity()).isEqualTo(3);
    }

    @Test
    void shouldUseValueEqualityForSameEventPayload() {
        UUID aggregateId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        TestItemAdded first = new TestItemAdded(
                aggregateId,
                itemId,
                3,
                OCCURRED_AT
        );

        TestItemAdded second = new TestItemAdded(
                aggregateId,
                itemId,
                3,
                OCCURRED_AT
        );

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldProduceDeterministicEventForSameInput() {
        UUID aggregateId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        TestItemAdded first = new TestItemAdded(
                aggregateId,
                itemId,
                1,
                OCCURRED_AT
        );

        TestItemAdded second = new TestItemAdded(
                aggregateId,
                itemId,
                1,
                OCCURRED_AT
        );

        assertThat(first).isEqualTo(second);
    }

    @Test
    void shouldRejectNullAggregateIdentity() {
        assertThatNullPointerException()
                .isThrownBy(() -> new TestItemAdded(
                        null,
                        UUID.randomUUID(),
                        1,
                        OCCURRED_AT
                ))
                .withMessage(
                        "Aggregate id must not be null"
                );
    }

    @Test
    void shouldRejectNullItemIdentity() {
        assertThatNullPointerException()
                .isThrownBy(() -> new TestItemAdded(
                        UUID.randomUUID(),
                        null,
                        1,
                        OCCURRED_AT
                ))
                .withMessage(
                        "Item id must not be null"
                );
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TestItemAdded(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        0,
                        OCCURRED_AT
                ))
                .withMessage(
                        "Quantity must be positive"
                );
    }

    @Test
    void shouldRejectNullOccurrenceTime() {
        assertThatNullPointerException()
                .isThrownBy(() -> new TestItemAdded(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        1,
                        null
                ))
                .withMessage(
                        "Event occurrence time must not be null"
                );
    }
}
