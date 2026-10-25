package com.gestioncommerciale.backend.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.LigneCommande;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    // Toutes les statistiques ci-dessous ignorent les lignes des commandes annulées (ANNULE) ;
    // une commande sans statut (antérieure à son introduction) est considérée VALIDE.

    // Ventes (quantité * prix unitaire) ventilées par catégorie de produit.
    @Query("SELECT COALESCE(lc.produit.categorie, 'Non catégorisé'), "
            + "SUM(lc.quantite * lc.prixUnitaire) "
            + "FROM LigneCommande lc "
            + "WHERE (lc.commande.statut IS NULL "
            + "OR lc.commande.statut <> com.gestioncommerciale.backend.model.StatutCommande.ANNULE) "
            + "GROUP BY COALESCE(lc.produit.categorie, 'Non catégorisé') "
            + "ORDER BY SUM(lc.quantite * lc.prixUnitaire) DESC")
    List<Object[]> sumVentesParCategorie();

    // Top produits vendus par quantité cumulée (limité via le Pageable fourni par l'appelant).
    @Query("SELECT lc.produit.id, lc.produit.nom, "
            + "SUM(lc.quantite), SUM(lc.quantite * lc.prixUnitaire) "
            + "FROM LigneCommande lc "
            + "WHERE (lc.commande.statut IS NULL "
            + "OR lc.commande.statut <> com.gestioncommerciale.backend.model.StatutCommande.ANNULE) "
            + "GROUP BY lc.produit.id, lc.produit.nom "
            + "ORDER BY SUM(lc.quantite) DESC")
    List<Object[]> findTopProduits(Pageable pageable);

    // Quantité vendue ventilée par catégorie de produit (contrairement à sumVentesParCategorie,
    // qui porte sur la valeur monétaire des ventes, ceci compte les articles vendus).
    @Query("SELECT COALESCE(lc.produit.categorie, 'Non catégorisé'), SUM(lc.quantite) "
            + "FROM LigneCommande lc "
            + "WHERE (lc.commande.statut IS NULL "
            + "OR lc.commande.statut <> com.gestioncommerciale.backend.model.StatutCommande.ANNULE) "
            + "GROUP BY COALESCE(lc.produit.categorie, 'Non catégorisé') "
            + "ORDER BY SUM(lc.quantite) DESC")
    List<Object[]> sumQuantiteParCategorie();

}