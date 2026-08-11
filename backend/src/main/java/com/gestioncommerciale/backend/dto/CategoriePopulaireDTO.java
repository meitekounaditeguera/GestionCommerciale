package com.gestioncommerciale.backend.dto;

public class CategoriePopulaireDTO {

    private String categorie;
    private Long quantiteVendue;

    public CategoriePopulaireDTO() {
    }

    public CategoriePopulaireDTO(String categorie, Long quantiteVendue) {
        this.categorie = categorie;
        this.quantiteVendue = quantiteVendue;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Long getQuantiteVendue() {
        return quantiteVendue;
    }

    public void setQuantiteVendue(Long quantiteVendue) {
        this.quantiteVendue = quantiteVendue;
    }
}
