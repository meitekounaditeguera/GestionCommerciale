package com.gestioncommerciale.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestioncommerciale.backend.dto.CommandeDTO;
import com.gestioncommerciale.backend.service.CommandeService;
import com.gestioncommerciale.backend.service.FacturePdfService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "http://localhost:4200")
public class CommandeController {

    private final CommandeService commandeService;
    private final FacturePdfService facturePdfService;

    public CommandeController(CommandeService commandeService, FacturePdfService facturePdfService) {
        this.commandeService = commandeService;
        this.facturePdfService = facturePdfService;
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

    // Télécharge la facture PDF archivée côté serveur, générée automatiquement à la création
    // de la commande (cf. CommandeServiceImpl.saveCommande). Mêmes rôles que le reste de
    // /api/commandes/** (cf. SecurityConfig) : pas de restriction supplémentaire ici.
    @Operation(summary = "Télécharger la facture PDF archivée d'une commande")
    @GetMapping(value = "/{id}/facture", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getFacture(@PathVariable Long id) {
        byte[] contenu = facturePdfService.lireFacture(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"facture-commande-" + id + ".pdf\"")
                .body(contenu);
    }
}