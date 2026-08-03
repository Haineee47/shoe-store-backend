package com.shoestore.shared.infrastructure.time;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TimeConfigurationTest {

    private final TimeConfiguration configuration =
            new TimeConfiguration();

    @Test
    void shouldProvideUtcApplicationClock() {
        Clock clock = configuration.applicationClock();

        assertThat(clock).isNotNull();
        assertThat(clock.getZone()).isEqualTo(ZoneOffset.UTC);
    }

    @Test
    void shouldProvideSystemUtcClock() {
        Clock clock = configuration.applicationClock();

        assertThat(clock)
                .isEqualTo(Clock.systemUTC());
    }
}
