package com.shoestore.shared.domain.model;

import java.util.UUID;

final class TestDomainEntity extends DomainEntity<UUID> {

    private String name;

    TestDomainEntity(UUID id, String name) {
        super(id);
        this.name = requireName(name);
    }

    String name() {
        return name;
    }

    void rename(String name) {
        this.name = requireName(name);
    }

    private static String requireName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Test entity name must not be null"
            );
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException(
                    "Test entity name must not be blank"
            );
        }

        return name;
    }
}
