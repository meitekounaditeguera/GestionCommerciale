package com.gestioncommerciale.backend.exception;

// Levée lorsqu'une transition de statut est demandée alors que la commande fournisseur
// n'est pas dans un état qui le permet (ex: réceptionner une commande déjà annulée).
public class StatutCommandeFournisseurInvalideException extends RuntimeException {
    public StatutCommandeFournisseurInvalideException(String message) {
        super(message);
    }
}
