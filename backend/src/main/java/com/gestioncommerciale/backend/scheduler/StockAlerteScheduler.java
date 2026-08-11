package com.gestioncommerciale.backend.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gestioncommerciale.backend.service.NotificationService;

// Déclenche l'alerte stock quotidienne. Séparé de NotificationService pour que celui-ci
// reste un simple service testable, sans dépendre du contexte de planification Spring.
@Component
public class StockAlerteScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockAlerteScheduler.class);

    private final NotificationService notificationService;

    public StockAlerteScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Cron configurable via ALERTE_STOCK_CRON (application.properties) : tous les jours
    // à 8h00 par défaut.
    @Scheduled(cron = "${alerte.stock.cron}")
    public void declencherAlerteStock() {
        try {
            notificationService.envoyerAlerteStock();
        } catch (Exception ex) {
            // Une panne SMTP (mal configurée, indisponible...) ne doit jamais faire
            // planter l'application : on journalise et on retentera au prochain déclenchement.
            LOGGER.warn("Échec de l'envoi de l'alerte stock quotidienne : {}", ex.getMessage());
        }
    }
}
