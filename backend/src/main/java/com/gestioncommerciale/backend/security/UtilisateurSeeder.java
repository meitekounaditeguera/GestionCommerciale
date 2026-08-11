package com.gestioncommerciale.backend.security;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gestioncommerciale.backend.model.Utilisateur;
import com.gestioncommerciale.backend.repository.UtilisateurRepository;

// Crée les comptes de démonstration (un par rôle) au démarrage, pour que l'authentification
// fonctionne immédiatement sans configuration manuelle en base.
@Component
public class UtilisateurSeeder implements CommandLineRunner {

    // Anciens noms de comptes de démo, hérités du précédent schéma de rôles (MANAGER/USER),
    // à retirer avant toute lecture JPA classique de la table.
    private static final List<String> ANCIENS_COMPTES_DEMO = List.of("manager", "user");

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurSeeder(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Retire l'ancienne contrainte CHECK générée pour les valeurs ADMIN/MANAGER/USER,
        // devenue invalide depuis le renommage de l'enum Role vers GESTIONNAIRE/CAISSIER.
        utilisateurRepository.supprimerContrainteRoleObsolete();

        // Suppression en SQL natif : évite de désérialiser l'ancien rôle ("MANAGER"/"USER"),
        // devenu invalide depuis le renommage de l'enum Role vers GESTIONNAIRE/CAISSIER.
        utilisateurRepository.supprimerParUsernames(ANCIENS_COMPTES_DEMO);

        creerSiAbsent("admin", "admin123", Role.ADMIN);
        creerSiAbsent("gestionnaire", "gestionnaire123", Role.GESTIONNAIRE);
        creerSiAbsent("caissier", "caissier123", Role.CAISSIER);
    }

    private void creerSiAbsent(String username, String password, Role role) {
        if (utilisateurRepository.findByUsername(username).isPresent()) {
            return;
        }
        creerUtilisateur(username, password, role);
    }

    private void creerUtilisateur(String username, String password, Role role) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setPassword(passwordEncoder.encode(password));
        utilisateur.setRole(role);
        utilisateurRepository.save(utilisateur);
    }
}
