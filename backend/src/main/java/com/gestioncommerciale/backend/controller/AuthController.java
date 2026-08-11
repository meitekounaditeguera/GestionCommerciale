package com.gestioncommerciale.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncommerciale.backend.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Permet de vérifier si les identifiants fournis sont valides.
    private final AuthenticationManager authenticationManager;

    // Service utilisé pour créer le token JWT après une connexion réussie.
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Endpoint de connexion.
    // Quand l'utilisateur envoie son username et mot de passe,
    // si tout est correct, on lui donne un token JWT.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // authenticate() ne renvoie jamais un résultat "non authentifié" : en cas
            // d'échec (mauvais mot de passe, utilisateur inconnu...), il lève une exception.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            // Le rôle (ex: "ROLE_ADMIN") vient des autorités chargées depuis la base de données.
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_CAISSIER");

            // On génère un token JWT contenant le nom d'utilisateur et son rôle.
            String token = jwtService.generateToken(request.username(), role);
            return ResponseEntity.ok(new AuthResponse(token, role));

        } catch (AuthenticationException ex) {
            // Identifiants invalides ou utilisateur inconnu : une réponse 401 claire,
            // pas une erreur 500 générique.
            return ResponseEntity.status(401).body(new ErrorResponse("Identifiants invalides."));
        }
    }

    // Représente la requête de connexion envoyée par le client.
    public record AuthRequest(String username, String password) {
    }

    // Représente la réponse envoyée au client après une connexion réussie.
    // Le rôle est renvoyé en plus du token pour que le frontend puisse
    // l'utiliser directement sans avoir à décoder le JWT.
    public record AuthResponse(String token, String role) {
    }

    // Représente une réponse d'erreur simple envoyée au client.
    public record ErrorResponse(String message) {
    }
}
