package com.gestioncommerciale.backend.dto;

public class ProduitRuptureDTO {

    private Long id;
    private String nom;
    private Integer quantite;

    // Estimation du nombre de jours avant épuisement du stock, fondée sur la vélocité de
    // vente réelle du produit (DashboardServiceImpl.calculerJoursAvantRupture). null signifie
    // une estimation indéterminée (aucune vente sur la période de référence) : à distinguer
    // explicitement d'une valeur numérique côté frontend, plutôt que d'afficher un "0 jour"
    // trompeur.
    private Integer joursAvantRupture;

    public ProduitRuptureDTO() {
    }

    public ProduitRuptureDTO(Long id, String nom, Integer quantite, Integer joursAvantRupture) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.joursAvantRupture = joursAvantRupture;
    }

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

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Integer getJoursAvantRupture() {
        return joursAvantRupture;
    }

    public void setJoursAvantRupture(Integer joursAvantRupture) {
        this.joursAvantRupture = joursAvantRupture;
    }
}
