package com.gestioncommerciale.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom / raison sociale du fournisseur.
    @Column(nullable = false, length = 150)
    private String nom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    @Column(nullable = false)
    private LocalDate dateCreation;

    // Suppression logique : un fournisseur "supprimé" est désactivé, jamais retiré physiquement
    // de la base. Ses commandes fournisseur passées restent ainsi consultables (FK intacte),
    // et la suppression ne peut plus jamais échouer avec une erreur d'intégrité 409.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean actif = true;

    public Fournisseur() {
    }

    // Permet de créer un fournisseur avec les informations nécessaires.
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
    }

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
