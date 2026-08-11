package com.gestioncommerciale.backend.mapper;

import com.gestioncommerciale.backend.dto.LigneCommandeFournisseurDTO;
import com.gestioncommerciale.backend.model.LigneCommandeFournisseur;
import com.gestioncommerciale.backend.model.Produit;

public class LigneCommandeFournisseurMapper {

    public static LigneCommandeFournisseurDTO toDTO(LigneCommandeFournisseur ligne) {

        if (ligne == null) {
            return null;
        }

        LigneCommandeFournisseurDTO dto = new LigneCommandeFournisseurDTO();

        dto.setId(ligne.getId());
        dto.setQuantite(ligne.getQuantite());
        dto.setPrixAchatUnitaire(ligne.getPrixAchatUnitaire());

        if (ligne.getProduit() != null) {
            dto.setProduitId(ligne.getProduit().getId());
        }

        return dto;
    }

    public static LigneCommandeFournisseur toEntity(LigneCommandeFournisseurDTO dto) {

        if (dto == null) {
            return null;
        }

        LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();

        ligne.setId(dto.getId());
        ligne.setQuantite(dto.getQuantite());
        ligne.setPrixAchatUnitaire(dto.getPrixAchatUnitaire());

        if (dto.getProduitId() != null) {
            Produit produit = new Produit();
            produit.setId(dto.getProduitId());
            ligne.setProduit(produit);
        }

        return ligne;
    }

}
