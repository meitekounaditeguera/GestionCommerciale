package com.gestioncommerciale.backend.security;

import java.io.IOException;

import io.jsonwebtoken.JwtException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Service qui permet de créer et vérifier les tokens JWT.
    private final JwtService jwtService;

    // Service Spring Security qui connaît les utilisateurs enregistrés.
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // On récupère l'en-tête Authorization envoyé par le client.
        final String authHeader = request.getHeader("Authorization");

        // Si aucun token n'est envoyé, on laisse passer la requête.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Le token JWT est envoyé après le mot-clé "Bearer ".
        String jwt = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(jwt);

            // Si un username est trouvé et que personne n'est déjà connecté,
            // on vérifie le token et on crée une authentification Spring Security.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            // Token expiré, malformé ou signature invalide : on laisse la requête
            // continuer sans authentification plutôt que de renvoyer une erreur 500.
            SecurityContextHolder.clearContext();
        }

        // On continue le traitement de la requête après la vérification.
        filterChain.doFilter(request, response);
    }
}
