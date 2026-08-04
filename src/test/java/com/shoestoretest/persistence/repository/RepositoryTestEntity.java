package com.shoestoretest.persistence.repository;

import com.shoestore.shared.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "repository_test_entities")
public class RepositoryTestEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected RepositoryTestEntity() {
        // Required by JPA.
    }

    public RepositoryTestEntity(String name) {
        this.name = requireName(name);
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = requireName(name);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Repository test entity name must not be blank"
            );
        }

        return name;
    }
}
