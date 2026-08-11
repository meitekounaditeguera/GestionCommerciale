package com.gestioncommerciale.backend.exception;

public class FournisseurNotFoundException extends RuntimeException {
    public FournisseurNotFoundException(String message) {
        super(message);
    }
}
