package com.gestioncommerciale.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestioncommerciale.backend.dto.AuditLogDTO;
import com.gestioncommerciale.backend.model.TypeAction;

public interface AuditLogService {

    Page<AuditLogDTO> getAllLogs(Pageable pageable);

    // Enregistre une ligne d'audit pour l'utilisateur actuellement authentifié.
    // `entite` est un libellé ("Client", "Produit", "Commande"...), `details` une phrase
    // lisible décrivant précisément ce qui a changé.
    void enregistrer(TypeAction action, String entite, String details);

}
