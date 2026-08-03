package com.gestioncommerciale.backend.model;

//import jakarta.persistence.*; est une autre écriture qui permet d'importer tous les imports de cette classe.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

// Constructeur vide obligatoire pour JPA
    public Client() {
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
}