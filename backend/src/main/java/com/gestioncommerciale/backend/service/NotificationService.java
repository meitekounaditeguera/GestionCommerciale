package com.gestioncommerciale.backend.service;

public interface NotificationService {

    // Envoie un email récapitulant les produits en rupture ou en stock bas.
    // N'envoie rien si aucun produit n'est concerné.
    void envoyerAlerteStock();

}
