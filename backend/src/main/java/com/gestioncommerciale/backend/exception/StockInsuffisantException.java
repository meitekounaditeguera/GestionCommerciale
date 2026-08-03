package com.gestioncommerciale.backend.exception;

// Levée lorsque le stock disponible d'un produit est insuffisant pour honorer une commande.
public class StockInsuffisantException extends RuntimeException {

    public StockInsuffisantException(String message) {
        super(message);
    }
}
