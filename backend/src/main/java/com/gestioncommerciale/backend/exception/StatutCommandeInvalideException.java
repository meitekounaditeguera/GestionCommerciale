package com.gestioncommerciale.backend.exception;

// Levée lorsqu'une transition de statut est demandée alors que la commande de vente
// n'est pas dans un état qui le permet (ex: annuler une commande déjà annulée).
public class StatutCommandeInvalideException extends RuntimeException {
    public StatutCommandeInvalideException(String message) {
        super(message);
    }
}
