package com.gestioncommerciale.backend.exception;

public class CommandeFournisseurNotFoundException extends RuntimeException {
    public CommandeFournisseurNotFoundException(String message) {
        super(message);
    }
}
