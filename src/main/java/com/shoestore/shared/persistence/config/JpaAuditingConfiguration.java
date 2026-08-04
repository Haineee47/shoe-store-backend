package com.shoestore.shared.persistence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * Configures Spring Data JPA auditing for persistence-enabled profiles.
 */
@Configuration(proxyBeanMethods = false)
@Profile("!test")
@EnableJpaAuditing(dateTimeProviderRef = "jpaAuditingDateTimeProvider")
public class JpaAuditingConfiguration {

    /**
     * Supplies auditing timestamps from the application Clock.
     *
     * @param clock application clock
     * @return auditing date-time provider
     */
    @Bean
    DateTimeProvider jpaAuditingDateTimeProvider(Clock clock) {
        return () -> Optional.of(Instant.now(clock));
    }
}
