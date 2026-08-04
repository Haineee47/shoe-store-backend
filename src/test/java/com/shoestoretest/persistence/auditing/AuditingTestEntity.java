package com.shoestoretest.persistence.auditing;

import com.shoestore.shared.persistence.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditing_test_entities")
public class AuditingTestEntity extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected AuditingTestEntity() {
        // Required by JPA.
    }

    public AuditingTestEntity(String name) {
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
                    "Auditing test entity name must not be blank"
            );
        }

        return name;
    }
}
