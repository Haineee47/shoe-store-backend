package com.shoestore.shared.domain.service;

/**
 * Marker interface for stateless domain services containing business logic
 * that does not naturally belong to a single entity or value object.
 *
 * <p>Implementations must remain free from application orchestration,
 * persistence, web, messaging, transaction, and framework concerns.</p>
 */
public interface DomainService {
}
