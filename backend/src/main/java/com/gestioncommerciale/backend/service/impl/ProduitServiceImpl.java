package com.gestioncommerciale.backend.service.impl;

import com.gestioncommerciale.backend.dto.ProduitDTO;
import com.gestioncommerciale.backend.exception.ProduitNotFoundException;
import com.gestioncommerciale.backend.mapper.ProduitMapper;
import com.gestioncommerciale.backend.model.Produit;
import com.gestioncommerciale.backend.model.TypeAction;
import com.gestioncommerciale.backend.repository.ProduitRepository;
import com.gestioncommerciale.backend.service.AuditLogService;
import com.gestioncommerciale.backend.service.ProduitService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProduitServiceImpl implements ProduitService {

    // Injection du repository
    private final ProduitRepository produitRepository;
    private final AuditLogService auditLogService;

    public ProduitServiceImpl(ProduitRepository produitRepository, AuditLogService auditLogService) {
        this.produitRepository = produitRepository;
        this.auditLogService = auditLogService;
    }

    // Retourne les produits de la page demandée.
    @Override
    public Page<ProduitDTO> getAllProduits(Pageable pageable) {

        return produitRepository
                .findByActifTrue(pageable)
                .map(ProduitMapper::toDTO);

    }

    // Recherche un produit par son id
    @Override
    public ProduitDTO getProduitById(Long id) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() ->
                        new ProduitNotFoundException("Produit introuvable"));

        return ProduitMapper.toDTO(produit);

    }

    // Recherche un produit par son code-barres/QR code (utilisé par le scan caméra).
    @Override
    public ProduitDTO getProduitByCodeBarre(String codeBarre) {

        Produit produit = produitRepository.findByCodeBarreAndActifTrue(codeBarre)
                .orElseThrow(() ->
                        new ProduitNotFoundException("Aucun produit ne correspond à ce code : " + codeBarre));

        return ProduitMapper.toDTO(produit);

    }

    // Ajoute un nouveau produit
    @Override
    public ProduitDTO saveProduit(ProduitDTO produitDTO) {

        Produit produit = ProduitMapper.toEntity(produitDTO);

        Produit savedProduit = produitRepository.save(produit);

        auditLogService.enregistrer(TypeAction.CREATION, "Produit",
                "Le produit " + savedProduit.getNom() + " a été créé (stock initial : " + savedProduit.getQuantite() + ")");

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
        produit.setCategorie(produitDTO.getCategorie());
        produit.setCodeBarre(produitDTO.getCodeBarre());

        Produit updatedProduit = produitRepository.save(produit);

        auditLogService.enregistrer(TypeAction.MODIFICATION, "Produit",
                "Le produit " + updatedProduit.getNom() + " a été mis à jour - Stock à " + updatedProduit.getQuantite());

        return ProduitMapper.toDTO(updatedProduit);

    }

    // Supprime un produit. Suppression logique : le produit est désactivé, jamais retiré
    // physiquement de la base, pour ne pas perdre l'historique des lignes de commande qui
    // le référencent (et éviter toute erreur d'intégrité 409 lors de la suppression).
    @Override
    public void deleteProduit(Long id) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable"));

        produit.setActif(false);
        produitRepository.save(produit);

        auditLogService.enregistrer(TypeAction.SUPPRESSION, "Produit",
                "Le produit " + produit.getNom() + " a été supprimé");

    }

}