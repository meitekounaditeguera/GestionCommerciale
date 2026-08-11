package com.gestioncommerciale.backend.mapper;

import com.gestioncommerciale.backend.dto.ProduitDTO;
import com.gestioncommerciale.backend.model.Produit;

public class ProduitMapper {

    // Convertit une entité Produit en ProduitDTO
    public static ProduitDTO toDTO(Produit produit) {

        if (produit == null) {
            return null;
        }

        ProduitDTO dto = new ProduitDTO();

        dto.setId(produit.getId());
        dto.setNom(produit.getNom());
        dto.setDescription(produit.getDescription());
        dto.setPrix(produit.getPrix());
        dto.setQuantite(produit.getQuantite());
        dto.setCategorie(produit.getCategorie());
        dto.setCodeBarre(produit.getCodeBarre());

        return dto;
    }

    // Convertit un ProduitDTO en entité Produit
    public static Produit toEntity(ProduitDTO dto) {

        if (dto == null) {
            return null;
        }

        Produit produit = new Produit();

        produit.setId(dto.getId());
        produit.setNom(dto.getNom());
        produit.setDescription(dto.getDescription());
        produit.setPrix(dto.getPrix());
        produit.setQuantite(dto.getQuantite());
        produit.setCategorie(dto.getCategorie());
        produit.setCodeBarre(dto.getCodeBarre());

        return produit;
    }

}