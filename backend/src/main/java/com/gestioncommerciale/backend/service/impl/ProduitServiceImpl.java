package com.gestioncommerciale.backend.service.impl;

import com.gestioncommerciale.backend.dto.ProduitDTO;
import com.gestioncommerciale.backend.exception.ProduitNotFoundException;
import com.gestioncommerciale.backend.mapper.ProduitMapper;
import com.gestioncommerciale.backend.model.Produit;
import com.gestioncommerciale.backend.repository.ProduitRepository;
import com.gestioncommerciale.backend.service.ProduitService;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduitServiceImpl implements ProduitService {

    // Injection du repository
    private final ProduitRepository produitRepository;

    public ProduitServiceImpl(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    // Retourne tous les produits
    @Override
    public List<ProduitDTO> getAllProduits() {

        return produitRepository
                .findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(ProduitMapper::toDTO)
                .toList();

    }

    // Recherche un produit par son id
    @Override
    public ProduitDTO getProduitById(Long id) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() ->
                        new ProduitNotFoundException("Produit introuvable"));

        return ProduitMapper.toDTO(produit);

    }

    // Ajoute un nouveau produit
    @Override
    public ProduitDTO saveProduit(ProduitDTO produitDTO) {

        Produit produit = ProduitMapper.toEntity(produitDTO);

        Produit savedProduit = produitRepository.save(produit);

        return ProduitMapper.toDTO(savedProduit);

    }

    // Modifie un produit existant
    @Override
    public ProduitDTO updateProduit(Long id, ProduitDTO produitDTO) {

        // On récupère d'abord le produit existant : sans cette vérification,
        // save() créerait silencieusement un nouveau produit si l'id n'existait pas.
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable"));

        produit.setNom(produitDTO.getNom());
        produit.setDescription(produitDTO.getDescription());
        produit.setPrix(produitDTO.getPrix());
        produit.setQuantite(produitDTO.getQuantite());

        Produit updatedProduit = produitRepository.save(produit);

        return ProduitMapper.toDTO(updatedProduit);

    }

    // Supprime un produit
    @Override
    public void deleteProduit(Long id) {

        if (!produitRepository.existsById(id)) {
            throw new ProduitNotFoundException("Produit introuvable");
        }

        produitRepository.deleteById(id);

    }

}