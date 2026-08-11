package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

public class MeilleurClientDTO {

    private String nom;
    private String prenom;
    private BigDecimal totalDepense;

    public MeilleurClientDTO() {
    }

    public MeilleurClientDTO(String nom, String prenom, BigDecimal totalDepense) {
        this.nom = nom;
        this.prenom = prenom;
        this.totalDepense = totalDepense;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public BigDecimal getTotalDepense() {
        return totalDepense;
    }

    public void setTotalDepense(BigDecimal totalDepense) {
        this.totalDepense = totalDepense;
    }
}
