package com.gestioncommerciale.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestioncommerciale.backend.dto.EntreeStockDTO;
import com.gestioncommerciale.backend.dto.MouvementStockDTO;
import com.gestioncommerciale.backend.model.Produit;

public interface MouvementStockService {

    // Réapprovisionnement manuel : augmente le stock du produit et journalise un mouvement ENTREE.
    MouvementStockDTO enregistrerEntree(EntreeStockDTO requete);

    // Journalise un mouvement SORTIE. Le stock est déjà décrémenté par l'appelant (ex: vente) :
    // cette méthode ne fait que tracer le mouvement dans l'historique.
    void enregistrerSortie(Produit produit, Integer quantite, String motif);

    // Journalise un mouvement ENTREE. Le stock est déjà réincrémenté par l'appelant (ex: annulation
    // de commande) : cette méthode ne fait que tracer le mouvement dans l'historique.
    void enregistrerEntreeAutomatique(Produit produit, Integer quantite, String motif);

    // Historique de tous les mouvements de stock, trié/paginé.
    Page<MouvementStockDTO> getHistorique(Pageable pageable);

}
