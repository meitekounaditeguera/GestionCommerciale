package com.gestioncommerciale.backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Clé secrète utilisée pour signer les tokens JWT.
    // Elle permet de vérifier que le token n'a pas été modifié.
    private final SecretKey secretKey;

    // Durée de vie du token en millisecondes.
    private final long jwtExpirationMs;

    public JwtService(@Value("${jwt.secret:}") String secret,
                      @Value("${jwt.expiration-ms:3600000}") long jwtExpirationMs) {
        // Si aucune secret key n'est donnée, on utilise une valeur par défaut.
        // En production, il faut mettre une vraie clé secrète dans les propriétés.
        String effectiveSecret = (secret == null || secret.isBlank())
                ? "gestion-commerciale-jwt-secret-key-2026-application"
                : secret;

        // On prépare la clé de signature du JWT.
        this.secretKey = buildSecretKey(effectiveSecret);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // Crée une clé secrète compatible avec la librairie JWT.
    private SecretKey buildSecretKey(String secret) {
        try {
            // Si la clé est déjà encodée en Base64, on la décode.
            return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        } catch (IllegalArgumentException ex) {
            // Sinon, on utilise la chaîne brute et on la complète si nécessaire.
            String normalized = secret;
            if (normalized.length() < 32) {
                normalized = normalized + "012345678901234567890123456789";
            }
            return Keys.hmacShaKeyFor(normalized.getBytes(StandardCharsets.UTF_8));
        }
    }

    // Génère un token JWT pour un utilisateur donné, en y intégrant son rôle
    // (ex: "ROLE_ADMIN") afin que le frontend puisse l'exploiter sans appel supplémentaire.
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username) // Le nom de l'utilisateur est stocké dans le token.
                .claim("role", role) // Rôle de l'utilisateur (ex: ROLE_ADMIN).
                .issuedAt(now)      // Date de création du token.
                .expiration(expiryDate) // Date d'expiration du token.
                .signWith(secretKey) // Signature cryptographique du token.
                .compact();
    }

    // Extrait le nom d'utilisateur depuis le token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Vérifie si le token est encore valide pour cet utilisateur.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Vérifie si le token a expiré.
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Récupère la date d'expiration du token.
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Méthode interne pour lire une information précise dans le token.
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}
