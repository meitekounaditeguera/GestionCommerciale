package com.gestioncommerciale.backend.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

//@RestControllerAdvice : indique que cette classe gère les exceptions pour tous les contrôleurs REST de l'application.
@RestControllerAdvice
public class GlobalExceptionHandler {

    //@ExceptionHandler(ClientNotFoundException.class) : indique que cette méthode gère les exceptions de type ClientNotFoundException.
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> handleClientNotFound(ClientNotFoundException ex) {

        //ResponseEntity : représente une réponse HTTP, y compris le statut, les en-têtes et le corps.
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // Gère les cas où le produit demandé n'existe pas.
    @ExceptionHandler(ProduitNotFoundException.class)
    public ResponseEntity<String> handleProduitNotFound(ProduitNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // Gère les cas où la commande demandée n'existe pas.
    @ExceptionHandler(CommandeNotFoundException.class)
    public ResponseEntity<String> handleCommandeNotFound(CommandeNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // Gère les cas où le stock disponible est insuffisant pour honorer une commande.
    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<String> handleStockInsuffisant(StockInsuffisantException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    // Gère les erreurs de validation des DTO (déclenchées par @Valid) et renvoie le détail par champ.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> erreurs = new LinkedHashMap<>();

        for (FieldError erreur : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(erreur.getField(), erreur.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erreurs);
    }

    // Gère un paramètre d'URL au mauvais format (ex: /api/clients/abc au lieu d'un id numérique).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Paramètre invalide : " + ex.getName());
    }

    // Gère les violations de contraintes en base (ex: suppression d'un client/produit
    // encore référencé par des commandes) plutôt que de laisser remonter une erreur 500.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Impossible d'effectuer cette opération : la ressource est utilisée ailleurs dans l'application.");
    }

    // Filet de sécurité pour toute exception imprévue : évite d'exposer la pile d'appels au client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur inattendue est survenue.");
    }

}