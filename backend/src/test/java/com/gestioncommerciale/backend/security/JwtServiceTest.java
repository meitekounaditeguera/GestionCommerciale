package com.gestioncommerciale.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

    @Test
    void shouldGenerateAndValidateToken() {
        JwtService jwtService = new JwtService("c2VjcmV0LWtleS1mb3ItZGV2ZWxvcG1lbnQtcHJvamVjdA==", 3600000L);
        UserDetails user = User.withUsername("admin")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        String token = jwtService.generateToken(user.getUsername(), "ROLE_ADMIN");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }
}
