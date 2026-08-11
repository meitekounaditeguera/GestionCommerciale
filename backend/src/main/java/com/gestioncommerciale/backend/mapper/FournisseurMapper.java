package com.gestioncommerciale.backend.mapper;

import com.gestioncommerciale.backend.dto.FournisseurDTO;
import com.gestioncommerciale.backend.model.Fournisseur;

public class FournisseurMapper {

    public static FournisseurDTO toDTO(Fournisseur fournisseur) {

        if (fournisseur == null) {
            return null;
        }

        FournisseurDTO dto = new FournisseurDTO();

        dto.setId(fournisseur.getId());
        dto.setNom(fournisseur.getNom());
        dto.setEmail(fournisseur.getEmail());
        dto.setTelephone(fournisseur.getTelephone());
        dto.setAdresse(fournisseur.getAdresse());
        dto.setDateCreation(fournisseur.getDateCreation());

        return dto;
    }

    public static Fournisseur toEntity(FournisseurDTO dto) {

        if (dto == null) {
            return null;
        }

        Fournisseur fournisseur = new Fournisseur();

        fournisseur.setId(dto.getId());
        fournisseur.setNom(dto.getNom());
        fournisseur.setEmail(dto.getEmail());
        fournisseur.setTelephone(dto.getTelephone());
        fournisseur.setAdresse(dto.getAdresse());
        fournisseur.setDateCreation(dto.getDateCreation());

        return fournisseur;
    }

}
