package com.gestioncommerciale.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.gestioncommerciale.backend.dto.EntreeStockDTO;
import com.gestioncommerciale.backend.dto.MouvementStockDTO;
import com.gestioncommerciale.backend.service.MouvementStockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Contrôleur pour gérer les opérations sur le stock
@Tag(
    name = "Stock",
    description = "API de gestion des mouvements de stock (entrées et sorties)"
)
@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    private final MouvementStockService mouvementStockService;

    public StockController(MouvementStockService mouvementStockService) {
        this.mouvementStockService = mouvementStockService;
    }

    @Operation(summary = "Enregistrer une entrée de stock (réapprovisionnement)")
    @PostMapping("/entree")
    @ResponseStatus(HttpStatus.CREATED)
    public MouvementStockDTO enregistrerEntree(@Valid @RequestBody EntreeStockDTO requete) {
        return mouvementStockService.enregistrerEntree(requete);
    }

    @Operation(summary = "Historique paginé des mouvements de stock, du plus récent au plus ancien")
    @GetMapping("/historique")
    public Page<MouvementStockDTO> getHistorique(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateMouvement").descending());
        return mouvementStockService.getHistorique(pageable);
    }

}
