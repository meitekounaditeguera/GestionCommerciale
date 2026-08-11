package com.gestioncommerciale.backend.dto;

public class ProduitPhareDTO {

    private String nom;
    private Long quantiteVendue;

    public ProduitPhareDTO() {
    }

    public ProduitPhareDTO(String nom, Long quantiteVendue) {
        this.nom = nom;
        this.quantiteVendue = quantiteVendue;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getQuantiteVendue() {
        return quantiteVendue;
    }

    public void setQuantiteVendue(Long quantiteVendue) {
        this.quantiteVendue = quantiteVendue;
    }
}
