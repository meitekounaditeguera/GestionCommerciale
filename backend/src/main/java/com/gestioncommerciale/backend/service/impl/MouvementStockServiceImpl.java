package com.gestioncommerciale.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioncommerciale.backend.dto.EntreeStockDTO;
import com.gestioncommerciale.backend.dto.MouvementStockDTO;
import com.gestioncommerciale.backend.exception.ProduitNotFoundException;
import com.gestioncommerciale.backend.mapper.MouvementStockMapper;
import com.gestioncommerciale.backend.model.MouvementStock;
import com.gestioncommerciale.backend.model.Produit;
import com.gestioncommerciale.backend.model.TypeMouvement;
import com.gestioncommerciale.backend.repository.MouvementStockRepository;
import com.gestioncommerciale.backend.repository.ProduitRepository;
import com.gestioncommerciale.backend.service.MouvementStockService;

@Service
public class MouvementStockServiceImpl implements MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final ProduitRepository produitRepository;

    public MouvementStockServiceImpl(
            MouvementStockRepository mouvementStockRepository,
            ProduitRepository produitRepository) {

        this.mouvementStockRepository = mouvementStockRepository;
        this.produitRepository = produitRepository;
    }

    @Override
    @Transactional
    public MouvementStockDTO enregistrerEntree(EntreeStockDTO requete) {

        Produit produit = produitRepository.findById(requete.getProduitId())
                .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable"));

        // Augmentation du stock disponible.
        produit.setQuantite(produit.getQuantite() + requete.getQuantite());
        produitRepository.save(produit);

        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setQuantite(requete.getQuantite());
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setMotif(requete.getMotif());

        MouvementStock enregistre = mouvementStockRepository.save(mouvement);

        return MouvementStockMapper.toDTO(enregistre);
    }

    @Override
    public void enregistrerSortie(Produit produit, Integer quantite, String motif) {

        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setQuantite(quantite);
        mouvement.setTypeMouvement(TypeMouvement.SORTIE);
        mouvement.setMotif(motif);

        mouvementStockRepository.save(mouvement);
    }

    @Override
    public void enregistrerEntreeAutomatique(Produit produit, Integer quantite, String motif) {

        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setQuantite(quantite);
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setMotif(motif);

        mouvementStockRepository.save(mouvement);
    }

    @Override
    public Page<MouvementStockDTO> getHistorique(Pageable pageable) {
        return mouvementStockRepository.findAll(pageable)
                .map(MouvementStockMapper::toDTO);
    }

}
