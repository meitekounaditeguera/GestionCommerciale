package com.gestioncommerciale.backend.service;

import java.util.List;

import com.gestioncommerciale.backend.dto.CommandeDTO;

public interface CommandeService {

    List<CommandeDTO> getAllCommandes();

    CommandeDTO getCommandeById(Long id);

    CommandeDTO saveCommande(CommandeDTO commandeDTO);

    void deleteCommande(Long id);

}