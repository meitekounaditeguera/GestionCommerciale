package com.gestioncommerciale.backend.exception;

// Levée lorsqu'aucune facture PDF n'a été archivée pour la commande demandée
// (commande créée avant l'introduction de cette fonctionnalité, ou génération initiale en échec).
public class FactureNotFoundException extends RuntimeException {

    public FactureNotFoundException(String message) {
        super(message);
    }
}
