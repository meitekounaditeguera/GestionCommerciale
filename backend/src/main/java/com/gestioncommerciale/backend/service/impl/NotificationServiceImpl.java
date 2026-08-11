package com.gestioncommerciale.backend.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.gestioncommerciale.backend.dto.ProduitRuptureDTO;
import com.gestioncommerciale.backend.service.DashboardService;
import com.gestioncommerciale.backend.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);

    private final DashboardService dashboardService;
    private final JavaMailSender mailSender;
    private final String destinataire;

    public NotificationServiceImpl(DashboardService dashboardService,
            JavaMailSender mailSender,
            @Value("${alerte.stock.destinataire}") String destinataire) {
        this.dashboardService = dashboardService;
        this.mailSender = mailSender;
        this.destinataire = destinataire;
    }

    @Override
    public void envoyerAlerteStock() {
        List<ProduitRuptureDTO> ruptures = dashboardService.getRupturesStock();

        // Rien à signaler : on n'envoie pas de mail pour ne pas habituer le destinataire
        // à ignorer des alertes qui arriveraient quotidiennement sans raison.
        if (ruptures.isEmpty()) {
            return;
        }

        List<ProduitRuptureDTO> ruptureImminente = ruptures.stream()
                .filter(produit -> produit.getQuantite() == 0)
                .collect(Collectors.toList());
        List<ProduitRuptureDTO> stockBas = ruptures.stream()
                .filter(produit -> produit.getQuantite() > 0)
                .collect(Collectors.toList());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject("[Gestion Commerciale] Alerte stock - " + ruptures.size() + " produit(s) à surveiller");
        message.setText(construireCorpsMessage(ruptureImminente, stockBas));

        mailSender.send(message);
    }

    // Email texte simple (pas de HTML) : suffisant pour une alerte interne, et plus fiable
    // face aux filtres anti-spam qu'un message HTML pour un envoi automatisé.
    private String construireCorpsMessage(List<ProduitRuptureDTO> ruptureImminente, List<ProduitRuptureDTO> stockBas) {
        StringBuilder corps = new StringBuilder();
        corps.append("Alerte stock - Gestion Commerciale\n");
        corps.append("Date : ").append(LocalDate.now().format(FORMAT_DATE)).append("\n\n");

        if (!ruptureImminente.isEmpty()) {
            corps.append("Produits en rupture (stock à 0) :\n");
            for (ProduitRuptureDTO produit : ruptureImminente) {
                corps.append("- ").append(produit.getNom()).append("\n");
            }
            corps.append("\n");
        }

        if (!stockBas.isEmpty()) {
            corps.append("Produits en stock bas :\n");
            for (ProduitRuptureDTO produit : stockBas) {
                corps.append("- ").append(produit.getNom())
                        .append(" (quantité restante : ").append(produit.getQuantite()).append(")\n");
            }
            corps.append("\n");
        }

        corps.append("Ceci est un message automatique envoyé par l'application Gestion Commerciale.");
        return corps.toString();
    }
}
