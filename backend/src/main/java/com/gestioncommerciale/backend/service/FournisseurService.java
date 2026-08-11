package com.gestioncommerciale.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestioncommerciale.backend.dto.FournisseurDTO;

public interface FournisseurService {

    Page<FournisseurDTO> getAllFournisseurs(Pageable pageable);

    FournisseurDTO getFournisseurById(Long id);

    FournisseurDTO saveFournisseur(FournisseurDTO fournisseurDTO);

    FournisseurDTO updateFournisseur(Long id, FournisseurDTO fournisseurDTO);

    void deleteFournisseur(Long id);

}
