package com.gestioncommerciale.backend.model;

//import jakarta.persistence.*; est une autre écriture qui permet d'importer tous les imports de cette classe.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "produits")
public class Produit {

    // Clé primaire générée automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom du produit
    @Column(nullable = false, length = 100)
    private String nom;

    // Description
    @Column(length = 255)
    private String description;

    // Prix
    @Column(nullable = false)
    private BigDecimal prix;

    // Quantité en stock
    @Column(nullable = false)
    private Integer quantite;

    // ==========================
    // Un produit peut être présent
    // dans plusieurs lignes de commande
    // ==========================
    @OneToMany(mappedBy = "produit")
    private List<LigneCommande> lignesCommande;

    // Constructeur vide obligatoire
    public Produit() {
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public List<LigneCommande> getLignesCommande() {
    return lignesCommande;
    }

    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }
    
}