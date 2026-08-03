package com.shoestore.shared.infrastructure.time;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Provides the application-wide source of current time.
 *
 * <p>Production components must depend on {@link Clock} rather than invoking
 * {@code Instant.now()} or using the system default time zone directly.</p>
 */
@Configuration(proxyBeanMethods = false)
public class TimeConfiguration {

    /**
     * Provides the production clock in UTC.
     *
     * @return application-wide UTC clock
     */
    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}
