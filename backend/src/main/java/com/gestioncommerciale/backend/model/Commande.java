package com.gestioncommerciale.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {

    // ==========================
    // Clé primaire
    // ==========================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================
    // Date de la commande
    // ==========================
    @Column(nullable = false)
    private LocalDate dateCommande;

    // ==========================
    // Montant total
    // ==========================
    @Column(nullable = false)
    private BigDecimal montantTotal;

    // ==========================
    // Client ayant passé la commande
    // Plusieurs commandes peuvent appartenir au même client.
    // ==========================
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // ==========================
    // Une commande contient plusieurs lignes
    // ==========================
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande;

    // ==========================
    // Constructeur vide obligatoire
    // ==========================
    public Commande() {
    }

    // ==========================
    // Getters et Setters
    // ==========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<LigneCommande> getLignesCommande() {
    return lignesCommande;
    }

    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }
    
}
