package com.gestioncommerciale.backend.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.gestioncommerciale.backend.dto.ProduitRuptureDTO;
import com.gestioncommerciale.backend.service.DashboardService;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void neDoitPasEnvoyerDeMailSiAucuneRuptureDeStock() {
        when(dashboardService.getRupturesStock()).thenReturn(Collections.emptyList());

        NotificationServiceImpl notificationService =
                new NotificationServiceImpl(dashboardService, mailSender, "alertes@gestion-commerciale.local", "");

        notificationService.envoyerAlerteStock();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void doitSeparerRuptureImminenteEtStockBasSelonLesSeuils() {
        when(dashboardService.getRupturesStock()).thenReturn(List.of(
                new ProduitRuptureDTO(1L, "Produit A", 0, null),
                new ProduitRuptureDTO(2L, "Produit B", 4, 3),
                new ProduitRuptureDTO(3L, "Produit C", 5, 6),
                new ProduitRuptureDTO(4L, "Produit D", 9, 12)));

        NotificationServiceImpl notificationService =
                new NotificationServiceImpl(dashboardService, mailSender, "alertes@gestion-commerciale.local", "");

        notificationService.envoyerAlerteStock();

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        String corps = captor.getValue().getText();

        int debutStockBas = corps.indexOf("Produits en stock bas");
        // 0 et 4 unités : rupture imminente (moins de 5) ; 5 et 9 unités : stock bas.
        assertThat(corps.indexOf("Produit A")).isLessThan(debutStockBas);
        assertThat(corps.indexOf("Produit B")).isLessThan(debutStockBas);
        assertThat(corps.indexOf("Produit C")).isGreaterThan(debutStockBas);
        assertThat(corps.indexOf("Produit D")).isGreaterThan(debutStockBas);
    }

    @Test
    void doitRenseignerLExpediteurQuandLaProprieteEstDefinie() {
        when(dashboardService.getRupturesStock()).thenReturn(List.of(
                new ProduitRuptureDTO(1L, "Produit A", 3, 2)));

        NotificationServiceImpl notificationService = new NotificationServiceImpl(dashboardService, mailSender,
                "alertes@gestion-commerciale.local", "stock@gestion-commerciale.local");

        notificationService.envoyerAlerteStock();

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getFrom()).isEqualTo("stock@gestion-commerciale.local");
    }

    @Test
    void neDoitPasRenseignerDExpediteurQuandLaProprieteEstVide() {
        when(dashboardService.getRupturesStock()).thenReturn(List.of(
                new ProduitRuptureDTO(1L, "Produit A", 3, 2)));

        NotificationServiceImpl notificationService =
                new NotificationServiceImpl(dashboardService, mailSender, "alertes@gestion-commerciale.local", "");

        notificationService.envoyerAlerteStock();

        // Comportement d'origine : pas de "From", le serveur SMTP applique le compte authentifié.
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getFrom()).isNull();
    }
}
