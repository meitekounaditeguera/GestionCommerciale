package com.gestioncommerciale.backend.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "lignes_commande_fournisseur")
public class LigneCommandeFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quantité commandée auprès du fournisseur.
    @Column(nullable = false)
    private Integer quantite;

    // Prix d'achat unitaire négocié avec le fournisseur (distinct du prix de vente du produit).
    @Column(nullable = false)
    private BigDecimal prixAchatUnitaire;

    @ManyToOne
    @JoinColumn(name = "commande_fournisseur_id", nullable = false)
    private CommandeFournisseur commandeFournisseur;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    public LigneCommandeFournisseur() {
    }

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

    public BigDecimal getPrixAchatUnitaire() {
        return prixAchatUnitaire;
    }

    public void setPrixAchatUnitaire(BigDecimal prixAchatUnitaire) {
        this.prixAchatUnitaire = prixAchatUnitaire;
    }

    public CommandeFournisseur getCommandeFournisseur() {
        return commandeFournisseur;
    }

    public void setCommandeFournisseur(CommandeFournisseur commandeFournisseur) {
        this.commandeFournisseur = commandeFournisseur;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
}
