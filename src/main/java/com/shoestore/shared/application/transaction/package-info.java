/**
 * Defines transaction boundary conventions for application use cases.
 *
 * <p>Transaction ownership belongs to application use-case implementations:
 * write use cases use read-write transactions, while database-backed query
 * use cases may use read-only transactions.</p>
 *
 * <p>Controllers, domain models, repository ports and response models must
 * not own transaction boundaries. Infrastructure persistence adapters
 * participate in the transaction opened by the application use case and
 * must not introduce independent transactions without an explicitly
 * approved use case.</p>
 *
 * <p>This package intentionally contains no custom transaction manager,
 * unit-of-work abstraction or transaction executor. Spring's declarative
 * transaction support is sufficient until a concrete requirement proves
 * otherwise.</p>
 */
package com.shoestore.shared.application.transaction;
