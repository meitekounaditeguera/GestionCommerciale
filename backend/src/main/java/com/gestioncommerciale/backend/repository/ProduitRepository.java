package com.gestioncommerciale.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Produit;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

}