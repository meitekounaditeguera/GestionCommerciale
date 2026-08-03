package com.gestioncommerciale.backend.controller;

import java.util.List;

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

    // Récupérer toutes les commandes
    @GetMapping
    public List<CommandeDTO> getAllCommandes() {
        return commandeService.getAllCommandes();
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
}