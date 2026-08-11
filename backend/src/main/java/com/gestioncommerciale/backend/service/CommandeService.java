package com.gestioncommerciale.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestioncommerciale.backend.dto.CommandeDTO;

public interface CommandeService {

    Page<CommandeDTO> getAllCommandes(Pageable pageable);

    CommandeDTO getCommandeById(Long id);

    CommandeDTO saveCommande(CommandeDTO commandeDTO);

    void deleteCommande(Long id);

    // Annule une commande VALIDE : passe son statut à ANNULE et recrédite le stock
    // de chaque produit concerné, dans une seule transaction.
    CommandeDTO annulerCommande(Long id);

}