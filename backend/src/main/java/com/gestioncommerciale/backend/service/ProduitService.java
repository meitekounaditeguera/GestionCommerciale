package com.gestioncommerciale.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestioncommerciale.backend.dto.ProduitDTO;

public interface ProduitService {

    Page<ProduitDTO> getAllProduits(Pageable pageable);

    ProduitDTO getProduitById(Long id);

    ProduitDTO getProduitByCodeBarre(String codeBarre);

    ProduitDTO saveProduit(ProduitDTO produitDTO);

    ProduitDTO updateProduit(Long id, ProduitDTO produitDTO);

    void deleteProduit(Long id);

}