package com.shoestoretest.persistence.mapping;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class MappingTestDetails {

    @Column(
            name = "external_reference",
            nullable = false,
            length = 64
    )
    private String externalReference;

    @Column(
            name = "note",
            nullable = true,
            length = 255
    )
    private String note;

    protected MappingTestDetails() {
        // Required by JPA.
    }

    public MappingTestDetails(
            String externalReference,
            String note
    ) {
        this.externalReference =
                requireExternalReference(externalReference);
        this.note = normalizeOptionalText(note);
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getNote() {
        return note;
    }

    private static String requireExternalReference(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "External reference must not be blank"
            );
        }

        if (value.length() > 64) {
            throw new IllegalArgumentException(
                    "External reference must not exceed 64 characters"
            );
        }

        return value;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > 255) {
            throw new IllegalArgumentException(
                    "Note must not exceed 255 characters"
            );
        }

        return normalized;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof MappingTestDetails that)) {
            return false;
        }

        return Objects.equals(
                externalReference,
                that.externalReference
        ) && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalReference, note);
    }
}
