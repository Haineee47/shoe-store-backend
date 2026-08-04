package com.shoestoretest.persistence.mapping;

import com.shoestore.shared.persistence.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "mapping_test_entities")
public class MappingTestEntity extends BaseEntity {

    @Column(
            name = "display_name",
            nullable = false,
            length = 120
    )
    private String displayName;

    @Column(
            name = "description",
            nullable = true,
            length = 500
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 32
    )
    private MappingTestStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "externalReference",
                    column = @Column(
                            name = "external_reference",
                            nullable = false,
                            length = 64
                    )
            ),
            @AttributeOverride(
                    name = "note",
                    column = @Column(
                            name = "details_note",
                            nullable = true,
                            length = 255
                    )
            )
    })
    private MappingTestDetails details;

    protected MappingTestEntity() {
        // Required by JPA.
    }

    public MappingTestEntity(
            String displayName,
            String description,
            MappingTestStatus status,
            MappingTestDetails details
    ) {
        this.displayName = requireDisplayName(displayName);
        this.description = normalizeDescription(description);
        this.status = requireStatus(status);
        this.details = requireDetails(details);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public MappingTestStatus getStatus() {
        return status;
    }

    public MappingTestDetails getDetails() {
        return details;
    }

    private static String requireDisplayName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Display name must not be blank"
            );
        }

        if (value.length() > 120) {
            throw new IllegalArgumentException(
                    "Display name must not exceed 120 characters"
            );
        }

        return value;
    }

    private static String normalizeDescription(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > 500) {
            throw new IllegalArgumentException(
                    "Description must not exceed 500 characters"
            );
        }

        return normalized;
    }

    private static MappingTestStatus requireStatus(
            MappingTestStatus value
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Status must not be null"
            );
        }

        return value;
    }

    private static MappingTestDetails requireDetails(
            MappingTestDetails value
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Details must not be null"
            );
        }

        return value;
    }
}
