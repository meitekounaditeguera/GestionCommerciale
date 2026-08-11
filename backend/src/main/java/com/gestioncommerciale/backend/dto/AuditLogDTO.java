package com.gestioncommerciale.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {

    private Long id;

    private String utilisateur;

    // "CREATION", "MODIFICATION" ou "SUPPRESSION" (nom brut de l'enum TypeAction) : le
    // frontend traduit le libellé affiché et la couleur du badge, comme pour le statut
    // des commandes ("VALIDE"/"ANNULE").
    private String action;

    private String entite;

    private String details;

    private LocalDateTime dateAction;

}
