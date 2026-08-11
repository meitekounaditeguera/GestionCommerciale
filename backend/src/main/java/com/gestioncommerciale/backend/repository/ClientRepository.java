package com.gestioncommerciale.backend.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Client;

@Repository
//Client : l'entité que l'on veut gérer, Long : le type de la clé primaire (id).
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Nombre de clients enregistrés depuis la date donnée.
    long countByDateCreationGreaterThanEqual(LocalDate depuis);

    // Clients actifs uniquement : les clients supprimés (actif = false) sont désactivés,
    // pas retirés de la base, et ne doivent donc jamais apparaître dans les listings.
    Page<Client> findByActifTrue(Pageable pageable);

}