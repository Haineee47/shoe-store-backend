package com.shoestore.shared.domain.event;

import java.time.Instant;

/**
 * Represents an immutable business fact that has already occurred
 * inside the domain.
 *
 * <p>Domain events describe domain meaning only. They must remain
 * independent from Spring events, messaging systems, persistence,
 * serialization, transport protocols, and application orchestration.</p>
 */
public interface DomainEvent {

    /**
     * Returns the instant at which the represented business fact occurred.
     *
     * @return non-null occurrence time supplied by the domain operation
     */
    Instant occurredAt();
}
