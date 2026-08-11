package com.gestioncommerciale.backend.mapper;

import java.util.stream.Collectors;

import com.gestioncommerciale.backend.dto.CommandeFournisseurDTO;
import com.gestioncommerciale.backend.model.CommandeFournisseur;
import com.gestioncommerciale.backend.model.Fournisseur;

public class CommandeFournisseurMapper {

    public static CommandeFournisseurDTO toDTO(CommandeFournisseur commande) {

        if (commande == null) {
            return null;
        }

        CommandeFournisseurDTO dto = new CommandeFournisseurDTO();

        dto.setId(commande.getId());
        dto.setReference(commande.getReference());
        dto.setDateCommande(commande.getDateCommande());
        dto.setStatut(commande.getStatut());
        dto.setMontantTotal(commande.getMontantTotal());

        if (commande.getFournisseur() != null) {
            dto.setFournisseurId(commande.getFournisseur().getId());
        }

        if (commande.getLignes() != null) {

            dto.setLignes(
                commande.getLignes()
                        .stream()
                        .map(LigneCommandeFournisseurMapper::toDTO)
                        .collect(Collectors.toList())
            );

        }

        return dto;
    }

    public static CommandeFournisseur toEntity(CommandeFournisseurDTO dto) {

        if (dto == null) {
            return null;
        }

        CommandeFournisseur commande = new CommandeFournisseur();

        commande.setId(dto.getId());
        commande.setReference(dto.getReference());
        commande.setDateCommande(dto.getDateCommande());
        commande.setStatut(dto.getStatut());
        commande.setMontantTotal(dto.getMontantTotal());

        if (dto.getFournisseurId() != null) {

            Fournisseur fournisseur = new Fournisseur();
            fournisseur.setId(dto.getFournisseurId());

            commande.setFournisseur(fournisseur);

        }

        return commande;
    }

}
