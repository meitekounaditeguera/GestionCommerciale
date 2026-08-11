package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

public class VentesParCategorieDTO {

    private String categorie;
    private BigDecimal totalVentes;

    public VentesParCategorieDTO() {
    }

    public VentesParCategorieDTO(String categorie, BigDecimal totalVentes) {
        this.categorie = categorie;
        this.totalVentes = totalVentes;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public BigDecimal getTotalVentes() {
        return totalVentes;
    }

    public void setTotalVentes(BigDecimal totalVentes) {
        this.totalVentes = totalVentes;
    }
}
