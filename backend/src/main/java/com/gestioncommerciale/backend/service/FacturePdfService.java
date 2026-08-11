package com.gestioncommerciale.backend.service;

import com.gestioncommerciale.backend.model.Commande;

public interface FacturePdfService {

    // Génère le PDF de la facture pour la commande donnée et l'archive sur le disque.
    void genererEtArchiver(Commande commande);

    // Charge le contenu binaire de la facture déjà archivée pour cette commande.
    // Lève FactureNotFoundException si aucun fichier n'existe pour cet id.
    byte[] lireFacture(Long commandeId);

}
