package com.shoestore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ProfileConfigurationTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldActivateTestProfile() {
        assertThat(environment.getActiveProfiles())
                .containsExactly("test");
    }

    @Test
    void shouldLoadTestProfileConfiguration() {
        assertThat(environment.getProperty("spring.main.banner-mode"))
                .isEqualTo("off");
    }
}
