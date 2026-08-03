package com.gestioncommerciale.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.LigneCommande;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

}