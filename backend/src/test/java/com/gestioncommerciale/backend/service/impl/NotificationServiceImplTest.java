package com.gestioncommerciale.backend.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

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
                new NotificationServiceImpl(dashboardService, mailSender, "alertes@gestion-commerciale.local");

        notificationService.envoyerAlerteStock();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
