package com.gestioncommerciale.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

// Une ligne du journal d'audit : trace une action de création, modification ou suppression
// réalisée par un utilisateur sur une entité métier (Client, Produit, Commande...).
// Ecrite une seule fois, jamais modifiée ni supprimée ensuite (pas de setters sur les
// champs métier une fois construite, en dehors de ceux exigés par JPA).
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de l'utilisateur authentifié à l'origine de l'action ("Système" si aucun
    // utilisateur n'est authentifié, par exemple un traitement interne).
    @Column(nullable = false, length = 100)
    private String utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeAction action;

    // Type d'entité concernée : "Client", "Produit", "Commande"...
    @Column(nullable = false, length = 50)
    private String entite;

    // Description lisible de l'action, ex: "Le produit iPhone 14 Pro a été mis à jour - Stock à 0".
    @Column(nullable = false, length = 500)
    private String details;

    @Column(nullable = false)
    private LocalDateTime dateAction;

    public AuditLog() {
    }

    @PrePersist
    protected void onCreate() {
        if (dateAction == null) {
            dateAction = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public TypeAction getAction() {
        return action;
    }

    public void setAction(TypeAction action) {
        this.action = action;
    }

    public String getEntite() {
        return entite;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
}
