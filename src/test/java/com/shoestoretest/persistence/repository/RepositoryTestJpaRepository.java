package com.shoestoretest.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositoryTestJpaRepository
        extends JpaRepository<RepositoryTestEntity, UUID> {
}
