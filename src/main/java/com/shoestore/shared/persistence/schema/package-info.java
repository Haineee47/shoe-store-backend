/**
 * MySQL schema conventions owned and applied through Flyway migrations.
 *
 * <p>Production tables use InnoDB, utf8mb4, explicit nullability and named
 * constraints. Hibernate validates mappings against the schema but must not
 * create or update database objects.</p>
 *
 * <p>This package contains schema documentation only. It must not contain
 * business tables, generic DDL generators, runtime migration logic or
 * database-specific business rules.</p>
 */
package com.shoestore.shared.persistence.schema;
