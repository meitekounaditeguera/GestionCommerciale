package com.gestioncommerciale.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestioncommerciale.backend.dto.CommandeFournisseurDTO;

public interface CommandeFournisseurService {

    Page<CommandeFournisseurDTO> getAllCommandesFournisseurs(Pageable pageable);

    CommandeFournisseurDTO getCommandeFournisseurById(Long id);

    CommandeFournisseurDTO creerCommandeFournisseur(CommandeFournisseurDTO commandeFournisseurDTO);

    // BROUILLON -> VALIDEE
    CommandeFournisseurDTO validerCommandeFournisseur(Long id);

    // BROUILLON ou VALIDEE -> ANNULEE
    CommandeFournisseurDTO annulerCommandeFournisseur(Long id);

    // BROUILLON ou VALIDEE -> LIVREE : met à jour le stock et journalise une entrée par ligne.
    CommandeFournisseurDTO recevoirCommandeFournisseur(Long id);

}
