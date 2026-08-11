package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

public class TopProduitDTO {

    private Long produitId;
    private String nom;
    private Long quantiteVendue;
    private BigDecimal chiffreAffaires;

    public TopProduitDTO() {
    }

    public TopProduitDTO(Long produitId, String nom, Long quantiteVendue, BigDecimal chiffreAffaires) {
        this.produitId = produitId;
        this.nom = nom;
        this.quantiteVendue = quantiteVendue;
        this.chiffreAffaires = chiffreAffaires;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
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

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }
}
