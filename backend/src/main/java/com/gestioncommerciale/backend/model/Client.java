package com.gestioncommerciale.backend.model;

//import jakarta.persistence.*; est une autre écriture qui permet d'importer tous les imports de cette classe.
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients")
public class Client {


    //@Id. Cette annotation indique la clé primaire de la table.
    //@GeneratedValue. Elle dit à PostgreSQL : C'est toi qui génères automatiquement les identifiants.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column. Elle permet de définir les contraintes de chaque colonne.
    //Par exemple : cette colonne est obligatoire.
    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    //Par exemple :deux clients ne peuvent pas avoir le même email.
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    // Date d'enregistrement du client (utilisée pour le suivi des nouveaux clients)
    // Nullable au niveau BDD pour ne pas casser les lignes existantes lors de la migration ddl-auto=update.
    private LocalDate dateCreation;

    // Suppression logique : un client "supprimé" est désactivé, jamais retiré physiquement
    // de la base. Ses commandes passées restent ainsi consultables (FK client_id intacte),
    // et la suppression ne peut plus jamais échouer avec une erreur d'intégrité 409.
    // columnDefinition avec valeur par défaut : les lignes déjà en base restent actives
    // lors de la migration ddl-auto=update (pas de NOT NULL sans valeur qui ferait échouer l'ALTER TABLE).
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean actif = true;

// Constructeur vide obligatoire pour JPA
    public Client() {
    }

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
    }

    //getters et setters pour chaque attribut de la classe Client.
    //Ces méthodes permettent d'accéder et de modifier les valeurs des attributs.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
    return prenom;
    }

    public void setPrenom(String prenom) {
    this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}