package com.gestioncommerciale.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gestioncommerciale.backend.dto.AuditLogDTO;
import com.gestioncommerciale.backend.mapper.AuditLogMapper;
import com.gestioncommerciale.backend.model.AuditLog;
import com.gestioncommerciale.backend.model.TypeAction;
import com.gestioncommerciale.backend.repository.AuditLogRepository;
import com.gestioncommerciale.backend.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public Page<AuditLogDTO> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(AuditLogMapper::toDTO);
    }

    @Override
    public void enregistrer(TypeAction action, String entite, String details) {

        AuditLog log = new AuditLog();

        log.setUtilisateur(utilisateurCourant());
        log.setAction(action);
        log.setEntite(entite);
        log.setDetails(details);

        auditLogRepository.save(log);
    }

    // Nom de l'utilisateur authentifié à l'origine de l'action (extrait du contexte de
    // sécurité posé par le filtre JWT), ou "Système" si l'appel n'est pas rattaché à une
    // requête authentifiée.
    private String utilisateurCourant() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "Système";
        }

        return authentication.getName();
    }

}
