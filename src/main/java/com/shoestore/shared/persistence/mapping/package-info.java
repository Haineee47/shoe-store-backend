/**
 * Shared JPA mapping conventions.
 *
 * <p>Persistent entities use field access and explicitly declare important
 * table and column names. Database schema ownership remains with Flyway;
 * Hibernate validates the schema and does not create or update it.</p>
 *
 * <p>This package must not contain business entities, generic entity mappers,
 * custom naming strategies without a demonstrated need, or database-specific
 * column definitions used as a substitute for migrations.</p>
 */
package com.shoestore.shared.persistence.mapping;
