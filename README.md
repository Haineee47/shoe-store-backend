# Shoe Store Backend

Backend REST API for a production-oriented shoe store application.

## Technology Baseline

- Java 21
- Spring Boot 4.1.0
- Maven 3.9.16
- MySQL 8.4.5 LTS
- Spring Data JPA
- Flyway
- Spring Security
- JWT
- Docker
- Testcontainers

## Architecture

- Modular Monolith
- Layered Architecture
- Clean Architecture Principles
- DDD Lite

## Project Status

Phase 1.8 — Code Quality & Build Enforcement.

## Build

mvnw.cmd clean verify

--------

Bổ sung phần chạy local:

## Run Locally

mvnw.cmd spring-boot:run

------------

## Runtime Profiles

The application supports the following runtime profiles:

- `local` — local development
- `test` — automated testing
- `prod` — production

No runtime profile is hardcoded in the application artifact.

### Run with the local profile

mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"

----------

## Local MySQL Infrastructure

The local database runs in Docker using MySQL Community Server 8.4.5.

### Prerequisites

- Docker Desktop
- Docker Compose v2

### Environment setup

Create the local environment file:

copy .env.example .env

Start MySQL

docker compose up -d mysql

Check status

docker compose ps

The MySQL service must report a healthy status.

View logs

docker compose logs --tail=100 mysql

Stop MySQL

docker compose stop mysql

Remove containers while preserving data

docker compose down

Destructive local reset

docker compose down -v

-----------

## Persistence

The application uses:

- Spring Data JPA
- Hibernate ORM
- HikariCP
- MySQL Connector/J
- Flyway

Flyway is the exclusive owner of database schema changes.

Hibernate must not create, update, or delete schema objects.

Runtime schema validation uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate

-----------

## Integration Testing

Persistence integration tests use MySQL 8.4.5 through Testcontainers.

The tests do not depend on:

- the local Docker Compose MySQL service;
- port `3307`;
- `.env`;
- a pre-existing database;
- H2 or another in-memory database.

Run all tests:

```bat
mvnw.cmd clean test


-------------------

## Build Enforcement

The Maven build enforces the locked project toolchain:

- Apache Maven 3.9.16
- Oracle JDK 21.0.6
- Java release 21
- UTF-8 source and reporting encoding

Always use the committed Maven Wrapper:

```bat
mvnw.cmd clean verify
