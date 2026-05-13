package com.club.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtConfigTest {

    @Test
    void rejectsMissingSecret() {
        JwtConfig config = new JwtConfig();
        config.setSecret("");
        config.setExpiration(86400000);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    void rejectsShortSecret() {
        JwtConfig config = new JwtConfig();
        config.setSecret("short-secret");
        config.setExpiration(86400000);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    void acceptsStrongSecret() {
        JwtConfig config = new JwtConfig();
        config.setSecret("01234567890123456789012345678901");
        config.setExpiration(86400000);

        assertDoesNotThrow(config::validate);
    }
}
