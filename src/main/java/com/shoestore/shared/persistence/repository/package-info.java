/**
 * Shared persistence repository conventions.
 *
 * <p>This package must not contain a generic domain repository or a shared
 * CRUD abstraction. Spring Data repositories belong to the infrastructure
 * package of the business module that owns the persisted aggregate.</p>
 *
 * <p>Domain repository ports must remain framework-independent and must not
 * extend Spring Data repository interfaces.</p>
 */
package com.shoestore.shared.persistence.repository;
