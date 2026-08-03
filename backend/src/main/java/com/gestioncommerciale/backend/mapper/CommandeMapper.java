package com.gestioncommerciale.backend.mapper;

import java.util.stream.Collectors;

import com.gestioncommerciale.backend.dto.CommandeDTO;
import com.gestioncommerciale.backend.model.Client;
import com.gestioncommerciale.backend.model.Commande;

public class CommandeMapper {

    public static CommandeDTO toDTO(Commande commande) {

        if (commande == null) {
            return null;
        }

        CommandeDTO dto = new CommandeDTO();

        dto.setId(commande.getId());
        dto.setDateCommande(commande.getDateCommande());
        dto.setMontantTotal(commande.getMontantTotal());

        if (commande.getClient() != null) {
            dto.setClientId(commande.getClient().getId());
        }

        if (commande.getLignesCommande() != null) {

            dto.setLignes(
                commande.getLignesCommande()
                        .stream()
                        .map(LigneCommandeMapper::toDTO)
                        .collect(Collectors.toList())
            );

        }

        return dto;
    }

    public static Commande toEntity(CommandeDTO dto) {

        if (dto == null) {
            return null;
        }

        Commande commande = new Commande();

        commande.setId(dto.getId());
        commande.setDateCommande(dto.getDateCommande());
        commande.setMontantTotal(dto.getMontantTotal());

        if (dto.getClientId() != null) {

            Client client = new Client();
            client.setId(dto.getClientId());

            commande.setClient(client);

        }

        return commande;
    }

}