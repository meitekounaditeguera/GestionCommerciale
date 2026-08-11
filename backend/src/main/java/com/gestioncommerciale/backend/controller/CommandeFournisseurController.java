package com.gestioncommerciale.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.gestioncommerciale.backend.dto.CommandeFournisseurDTO;
import com.gestioncommerciale.backend.service.CommandeFournisseurService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Commandes fournisseurs", description = "API de gestion du cycle d'achat auprès des fournisseurs")
@RestController
@RequestMapping("/api/commandes-fournisseurs")
@CrossOrigin(origins = "http://localhost:4200")
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeFournisseurService;

    public CommandeFournisseurController(CommandeFournisseurService commandeFournisseurService) {
        this.commandeFournisseurService = commandeFournisseurService;
    }

    @Operation(summary = "Lister toutes les commandes fournisseurs")
    @GetMapping
    public Page<CommandeFournisseurDTO> getAllCommandesFournisseurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return commandeFournisseurService.getAllCommandesFournisseurs(pageable);
    }

    @Operation(summary = "Rechercher une commande fournisseur par son identifiant")
    @GetMapping("/{id}")
    public CommandeFournisseurDTO getCommandeFournisseurById(@PathVariable Long id) {
        return commandeFournisseurService.getCommandeFournisseurById(id);
    }

    @Operation(summary = "Créer une nouvelle commande fournisseur (statut initial : BROUILLON)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeFournisseurDTO creerCommandeFournisseur(@Valid @RequestBody CommandeFournisseurDTO commandeFournisseurDTO) {
        return commandeFournisseurService.creerCommandeFournisseur(commandeFournisseurDTO);
    }

    @Operation(summary = "Valider une commande fournisseur (BROUILLON -> VALIDEE)")
    @PatchMapping("/{id}/valider")
    public CommandeFournisseurDTO validerCommandeFournisseur(@PathVariable Long id) {
        return commandeFournisseurService.validerCommandeFournisseur(id);
    }

    @Operation(summary = "Annuler une commande fournisseur")
    @PatchMapping("/{id}/annuler")
    public CommandeFournisseurDTO annulerCommandeFournisseur(@PathVariable Long id) {
        return commandeFournisseurService.annulerCommandeFournisseur(id);
    }

    @Operation(summary = "Réceptionner la marchandise : passe la commande à LIVREE et met à jour le stock")
    @PostMapping("/{id}/recevoir")
    public CommandeFournisseurDTO recevoirCommandeFournisseur(@PathVariable Long id) {
        return commandeFournisseurService.recevoirCommandeFournisseur(id);
    }

}
