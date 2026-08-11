package com.gestioncommerciale.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.MouvementStock;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

}
