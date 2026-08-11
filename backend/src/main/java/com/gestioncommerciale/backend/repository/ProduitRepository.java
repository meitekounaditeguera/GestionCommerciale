package com.gestioncommerciale.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Produit;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    // Produits actifs dont le stock est inférieur ou égal au seuil (rupture / stock bas) :
    // un produit supprimé (actif = false) ne doit plus déclencher d'alerte de stock.
    List<Produit> findByActifTrueAndQuantiteLessThanEqualOrderByQuantiteAsc(Integer quantite);

    // Produits actifs uniquement : les produits supprimés (actif = false) sont désactivés,
    // pas retirés de la base, et ne doivent donc jamais apparaître dans les listings.
    Page<Produit> findByActifTrue(Pageable pageable);

    // Recherche par code-barres/QR code scanné (produits actifs uniquement).
    Optional<Produit> findByCodeBarreAndActifTrue(String codeBarre);

}