package com.gestioncommerciale.backend.service;

import com.gestioncommerciale.backend.dto.ProduitDTO;
import java.util.List;

public interface ProduitService {

    List<ProduitDTO> getAllProduits();

    ProduitDTO getProduitById(Long id);

    ProduitDTO saveProduit(ProduitDTO produitDTO);

    ProduitDTO updateProduit(Long id, ProduitDTO produitDTO);

    void deleteProduit(Long id);

}