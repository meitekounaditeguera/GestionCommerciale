package com.gestioncommerciale.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncommerciale.backend.dto.AuditLogDTO;
import com.gestioncommerciale.backend.service.AuditLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Journal d'audit", description = "Historique des actions de création, modification et suppression")
@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "http://localhost:4200")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // Journal d'audit, du plus récent au plus ancien. Réservé aux administrateurs
    // (cf. SecurityConfig : /api/audit-logs/** -> ROLE_ADMIN).
    @Operation(summary = "Lister le journal d'audit, du plus récent au plus ancien")
    @GetMapping
    public Page<AuditLogDTO> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateAction").descending());
        return auditLogService.getAllLogs(pageable);
    }

}
