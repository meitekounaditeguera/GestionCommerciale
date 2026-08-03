package com.gestioncommerciale.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Commande;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

}