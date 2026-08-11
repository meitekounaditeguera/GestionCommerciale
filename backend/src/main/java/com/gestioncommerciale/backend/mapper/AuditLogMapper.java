package com.gestioncommerciale.backend.mapper;

import com.gestioncommerciale.backend.dto.AuditLogDTO;
import com.gestioncommerciale.backend.model.AuditLog;

public class AuditLogMapper {

    public static AuditLogDTO toDTO(AuditLog log) {

        if (log == null) {
            return null;
        }

        AuditLogDTO dto = new AuditLogDTO();

        dto.setId(log.getId());
        dto.setUtilisateur(log.getUtilisateur());
        dto.setAction(log.getAction() != null ? log.getAction().name() : null);
        dto.setEntite(log.getEntite());
        dto.setDetails(log.getDetails());
        dto.setDateAction(log.getDateAction());

        return dto;
    }

}
