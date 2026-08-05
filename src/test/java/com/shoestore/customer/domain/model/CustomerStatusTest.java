package com.shoestore.customer.domain.model;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerStatusTest {

    @Test
    void shouldDefineActiveStatus() {
        assertThat(CustomerStatus.ACTIVE.name())
                .isEqualTo("ACTIVE");
    }

    @Test
    void shouldDefineInactiveStatus() {
        assertThat(CustomerStatus.INACTIVE.name())
                .isEqualTo("INACTIVE");
    }

    @Test
    void shouldContainOnlySupportedLifecycleStatuses() {
        assertThat(CustomerStatus.values())
                .containsExactly(
                        CustomerStatus.ACTIVE,
                        CustomerStatus.INACTIVE
                );
    }

    @Test
    void shouldResolveStatusFromStableName() {
        assertThat(CustomerStatus.valueOf("ACTIVE"))
                .isEqualTo(CustomerStatus.ACTIVE);

        assertThat(CustomerStatus.valueOf("INACTIVE"))
                .isEqualTo(CustomerStatus.INACTIVE);
    }

    @Test
    void shouldNotContainSecurityOrVerificationStatuses() {
        assertThat(
                Arrays.stream(CustomerStatus.values())
                        .map(Enum::name)
        ).doesNotContain(
                "PENDING_VERIFICATION",
                "BLOCKED",
                "SUSPENDED"
        );
    }

    @Test
    void shouldNotContainDeletionOrPrivacyStatuses() {
        assertThat(
                Arrays.stream(CustomerStatus.values())
                        .map(Enum::name)
        ).doesNotContain(
                "DELETED",
                "ANONYMIZED"
        );
    }
}
