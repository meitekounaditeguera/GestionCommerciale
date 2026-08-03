package com.gestioncommerciale.backend.exception;

// Levée lorsque la commande demandée n'existe pas en base de données.
public class CommandeNotFoundException extends RuntimeException {

    public CommandeNotFoundException(String message) {
        super(message);
    }
}
