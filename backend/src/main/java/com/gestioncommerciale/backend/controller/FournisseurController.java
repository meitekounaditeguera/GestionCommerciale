package com.gestioncommerciale.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestioncommerciale.backend.dto.FournisseurDTO;
import com.gestioncommerciale.backend.service.FournisseurService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Gestion des fournisseurs", description = "API permettant de gérer les fournisseurs de l'application")
@RestController
@RequestMapping("/api/fournisseurs")
@CrossOrigin(origins = "http://localhost:4200")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    public FournisseurController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @Operation(summary = "Lister tous les fournisseurs")
    @GetMapping
    public Page<FournisseurDTO> getAllFournisseurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return fournisseurService.getAllFournisseurs(pageable);
    }

    @Operation(summary = "Rechercher un fournisseur par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<FournisseurDTO> getFournisseurById(@PathVariable Long id) {
        return ResponseEntity.ok(fournisseurService.getFournisseurById(id));
    }

    @Operation(summary = "Créer un nouveau fournisseur")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FournisseurDTO createFournisseur(@Valid @RequestBody FournisseurDTO fournisseurDTO) {
        return fournisseurService.saveFournisseur(fournisseurDTO);
    }

    @Operation(summary = "Modifier un fournisseur")
    @PutMapping("/{id}")
    public FournisseurDTO updateFournisseur(@PathVariable Long id, @Valid @RequestBody FournisseurDTO fournisseurDTO) {
        return fournisseurService.updateFournisseur(id, fournisseurDTO);
    }

    @Operation(summary = "Supprimer un fournisseur")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFournisseur(@PathVariable Long id) {
        fournisseurService.deleteFournisseur(id);
    }

}
