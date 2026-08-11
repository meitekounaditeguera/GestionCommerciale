package com.gestioncommerciale.backend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Filtre qui lit le token JWT envoyé par le frontend et authentifie la requête.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Cette méthode configure les règles de sécurité de l'application.
    // Elle dit quelles routes sont publiques et lesquelles nécessitent un token.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Désactive CSRF pour une API REST simple.
                .authorizeHttpRequests(auth -> auth
                        // Ces routes sont accessibles sans token.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index", "/home").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                        // Les actions critiques (suppressions) sont réservées aux administrateurs.
                        .requestMatchers(HttpMethod.DELETE, "/api/clients/**", "/api/produits/**", "/api/commandes/**")
                        .hasRole("ADMIN")
                        // Le tableau de bord est réservé aux administrateurs et aux gestionnaires.
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                        // Les mouvements de stock (entrée, historique) sont réservés aux administrateurs et aux gestionnaires.
                        .requestMatchers("/api/stock/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                        // La prise de commande et la génération de facture sont accessibles aux 3 rôles métier.
                        .requestMatchers("/api/commandes/**").hasAnyRole("CAISSIER", "GESTIONNAIRE", "ADMIN")
                        // La gestion des fournisseurs et des commandes d'achat est réservée aux administrateurs et aux gestionnaires.
                        .requestMatchers("/api/fournisseurs/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                        .requestMatchers("/api/commandes-fournisseurs/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                        // Le journal d'audit trace les actions de tous les utilisateurs : réservé aux administrateurs.
                        .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                        // Le reste de l'API (lecture, création) est accessible à tout utilisateur authentifié.
                        .anyRequest().authenticated())
                // On ne garde pas de session pour cette API, car on utilise des JWT.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Le filtre JWT doit s'exécuter avant le filtre d'authentification standard,
                // sinon Spring Security ignore l'authentification qu'il pose dans le contexte.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // "Authorization" doit être listé explicitement : avec allowCredentials(true),
        // certains navigateurs n'acceptent pas "*" comme joker pour les en-têtes autorisés.
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Permet à Spring Security de gérer l'authentification des utilisateurs.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
