package com.gestioncommerciale.backend.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.MouvementStock;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    // Somme des quantités sorties (ventes) pour un produit depuis une date donnée : utilisée
    // pour calculer la vélocité de vente réelle (DashboardServiceImpl.calculerVelociteVente).
    // COALESCE(..., 0) : évite un résultat null quand le produit n'a aucun mouvement de sortie
    // sur la période (agrégat sur un jeu de résultats vide).
    //
    // Les ventes annulées (ou supprimées) sont écartées : une sortie "Vente - Commande #N" est
    // ignorée dès qu'il existe, pour le même produit, une entrée "Annulation - Commande #N"
    // (motifs posés par CommandeServiceImpl). SUBSTRING(m.motif, 6) retire le préfixe "Vente"
    // (5 caractères) pour ne garder que " - Commande #N", recollé derrière "Annulation".
    // L'appariement se fait vente par vente, sans borne de date sur l'annulation : une vente
    // annulée ne compte jamais, quelle que soit la date de l'annulation, et une annulation
    // dont la vente est antérieure à la période ne vient rien retrancher (pas de résultat négatif).
    @Query("SELECT COALESCE(SUM(m.quantite), 0) FROM MouvementStock m "
            + "WHERE m.produit.id = :produitId "
            + "AND m.typeMouvement = com.gestioncommerciale.backend.model.TypeMouvement.SORTIE "
            + "AND m.dateMouvement >= :depuis "
            + "AND NOT EXISTS (SELECT a.id FROM MouvementStock a "
            + "WHERE a.produit.id = m.produit.id "
            + "AND a.typeMouvement = com.gestioncommerciale.backend.model.TypeMouvement.ENTREE "
            + "AND a.motif = CONCAT('Annulation', SUBSTRING(m.motif, 6)))")
    Long sumSortiesDepuis(@Param("produitId") Long produitId, @Param("depuis") LocalDateTime depuis);

}
