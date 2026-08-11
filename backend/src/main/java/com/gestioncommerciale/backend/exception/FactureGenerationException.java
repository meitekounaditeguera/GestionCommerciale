package com.gestioncommerciale.backend.exception;

// Levée lorsque la génération ou la lecture du fichier PDF de la facture échoue (erreur
// d'écriture disque, etc.). Jamais renvoyée telle quelle au client lors de la création d'une
// commande : CommandeServiceImpl la journalise sans faire échouer la commande pour autant.
// Elle peut en revanche remonter en 500 générique si elle survient à la lecture d'une facture
// déjà archivée (cf. GlobalExceptionHandler, filet de sécurité pour Exception.class).
public class FactureGenerationException extends RuntimeException {

    public FactureGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
