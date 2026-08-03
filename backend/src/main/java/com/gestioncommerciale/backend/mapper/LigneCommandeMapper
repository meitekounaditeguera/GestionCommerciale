package com.gestioncommerciale.backend.mapper;

import com.gestioncommerciale.backend.dto.LigneCommandeDTO;
import com.gestioncommerciale.backend.model.LigneCommande;
import com.gestioncommerciale.backend.model.Produit;

public class LigneCommandeMapper {

    public static LigneCommandeDTO toDTO(LigneCommande ligne) {

        if (ligne == null) {
            return null;
        }

        LigneCommandeDTO dto = new LigneCommandeDTO();

        dto.setId(ligne.getId());
        dto.setQuantite(ligne.getQuantite());
        dto.setPrixUnitaire(ligne.getPrixUnitaire());

        if (ligne.getProduit() != null) {
            dto.setProduitId(ligne.getProduit().getId());
        }

        return dto;
    }

    public static LigneCommande toEntity(LigneCommandeDTO dto) {

        if (dto == null) {
            return null;
        }

        LigneCommande ligne = new LigneCommande();

        ligne.setId(dto.getId());
        ligne.setQuantite(dto.getQuantite());
        ligne.setPrixUnitaire(dto.getPrixUnitaire());

        if (dto.getProduitId() != null) {
            Produit produit = new Produit();
            produit.setId(dto.getProduitId());
            ligne.setProduit(produit);
        }

        return ligne;
    }

}