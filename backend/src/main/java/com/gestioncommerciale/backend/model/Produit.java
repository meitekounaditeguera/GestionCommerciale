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

    // Catégorie du produit (utilisée pour la ventilation des ventes par catégorie)
    @Column(length = 100)
    private String categorie;

    // Code-barres / QR code du produit, utilisé pour la recherche par scan caméra.
    // Facultatif : tous les produits n'ont pas de code physique.
    @Column(name = "code_barre", length = 100, unique = true)
    private String codeBarre;

    // Suppression logique : un produit "supprimé" est désactivé, jamais retiré physiquement
    // de la base. Les lignes de commande passées restent ainsi consultables (FK produit_id
    // intacte), et la suppression ne peut plus jamais échouer avec une erreur d'intégrité 409.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean actif = true;

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

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getCodeBarre() {
        return codeBarre;
    }

    public void setCodeBarre(String codeBarre) {
        // Normalise une chaîne vide en null : la colonne est UNIQUE, et sans cette
        // normalisation, un deuxième produit sans code-barres violerait la contrainte
        // (deux "" ne sont pas distincts pour SQL, contrairement à deux NULL).
        this.codeBarre = (codeBarre == null || codeBarre.isBlank()) ? null : codeBarre;
    }

    public List<LigneCommande> getLignesCommande() {
    return lignesCommande;
    }

    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

}