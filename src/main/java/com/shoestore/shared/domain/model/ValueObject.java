package com.shoestore.shared.domain.model;

/**
 * Marker interface for immutable domain values that are defined entirely
 * by their meaningful attributes and have no independent identity.
 *
 * <p>Implementations must preserve their invariants, provide value-based
 * equality, and remain free from framework and persistence concerns.</p>
 */
public interface ValueObject {
}
