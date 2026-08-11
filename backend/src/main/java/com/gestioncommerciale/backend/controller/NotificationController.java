package com.gestioncommerciale.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncommerciale.backend.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notifications", description = "Déclenchement des alertes envoyées par email")
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Déclenchement manuel de l'alerte stock, pour tester sans attendre le cron quotidien.
    // Réservé aux administrateurs (cf. SecurityConfig : /api/notifications/** -> ROLE_ADMIN).
    @Operation(summary = "Déclencher manuellement l'envoi de l'alerte stock par email")
    @PostMapping("/test-alerte-stock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void testAlerteStock() {
        notificationService.envoyerAlerteStock();
    }

}
