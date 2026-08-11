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
    @Query("SELECT COALESCE(SUM(m.quantite), 0) FROM MouvementStock m "
            + "WHERE m.produit.id = :produitId "
            + "AND m.typeMouvement = com.gestioncommerciale.backend.model.TypeMouvement.SORTIE "
            + "AND m.dateMouvement >= :depuis")
    Long sumSortiesDepuis(@Param("produitId") Long produitId, @Param("depuis") LocalDateTime depuis);

}
