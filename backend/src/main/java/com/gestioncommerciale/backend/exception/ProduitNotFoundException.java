package com.gestioncommerciale.backend.exception;

// Levée lorsque le produit demandé n'existe pas en base de données.
public class ProduitNotFoundException extends RuntimeException {

    public ProduitNotFoundException(String message) {
        super(message);
    }
}
