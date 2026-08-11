package com.gestioncommerciale.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Fournisseur;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    // Fournisseurs actifs uniquement : les fournisseurs supprimés (actif = false) sont
    // désactivés, pas retirés de la base, et ne doivent donc jamais apparaître dans les listings.
    Page<Fournisseur> findByActifTrue(Pageable pageable);

}
