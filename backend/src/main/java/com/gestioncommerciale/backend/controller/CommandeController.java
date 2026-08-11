package com.gestioncommerciale.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.gestioncommerciale.backend.dto.CommandeDTO;
import com.gestioncommerciale.backend.service.CommandeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "http://localhost:4200")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    // Récupérer les commandes de la page demandée
    @GetMapping
    public Page<CommandeDTO> getAllCommandes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return commandeService.getAllCommandes(pageable);
    }

    // Récupérer une commande par son id
    @GetMapping("/{id}")
    public CommandeDTO getCommandeById(@PathVariable Long id) {
        return commandeService.getCommandeById(id);
    }

    // Créer une nouvelle commande
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeDTO saveCommande(@Valid @RequestBody CommandeDTO commandeDTO) {
        return commandeService.saveCommande(commandeDTO);
    }

    // Supprimer une commande
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
    }

    // Annuler une commande : passe son statut à ANNULE et recrédite le stock des produits.
    @PutMapping("/{id}/annuler")
    public CommandeDTO annulerCommande(@PathVariable Long id) {
        return commandeService.annulerCommande(id);
    }
}