package com.gestioncommerciale.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Client;

@Repository
//Client : l'entité que l'on veut gérer, Long : le type de la clé primaire (id).
public interface ClientRepository extends JpaRepository<Client, Long> {

}