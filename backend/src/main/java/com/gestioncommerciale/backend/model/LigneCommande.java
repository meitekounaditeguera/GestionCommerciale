package com.gestioncommerciale.backend.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "ligne_commandes")
public class LigneCommande {

    // ==========================
    // Clé primaire
    // ==========================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================
    // Quantité commandée
    // ==========================
    @Column(nullable = false)
    private Integer quantite;

    // ==========================
    // Prix unitaire au moment de la commande
    // ==========================
    @Column(nullable = false)
    private BigDecimal prixUnitaire;

    // ==========================
    // Relation avec la commande
    // Plusieurs lignes appartiennent à une commande
    // ==========================
    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    // ==========================
    // Relation avec le produit
    // Plusieurs lignes peuvent référencer le même produit
    // ==========================
    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    // ==========================
    // Constructeur vide
    // ==========================
    public LigneCommande() {
    }

    // ==========================
    // Getters et Setters
    // ==========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
}