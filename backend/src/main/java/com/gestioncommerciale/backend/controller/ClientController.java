package com.gestioncommerciale.backend.controller;

import com.gestioncommerciale.backend.dto.ClientDTO;
import com.gestioncommerciale.backend.service.ClientService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

//@Tag : permet de regrouper les routes de l'API dans la documentation Swagger.
@Tag(
    name = "Gestion des clients",
    description = "API permettant de gérer les clients de l'application"
)

//@RestController : indique que cette classe expose une API REST.
//@RequestMapping("/api/clients") : toutes les routes commencent par /api/clients.
@RestController 
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:4200")

public class ClientController { 

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }   

    //@Operation(summary = "Récupérer tous les clients") : permet de documenter la route dans Swagger.
    @Operation(summary = "Lister tous les clients")

    //@GetMapping : récupérer tous les clients de la base de données.
    @GetMapping
    public Page<ClientDTO> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return clientService.getAllClients(pageable);
    }

    //@Operation(summary = "Récupérer Un client par son id") : permet de documenter la route dans Swagger.
    @Operation(summary = "Rechercher un client par son identifiant")

    //@GetMapping("/{id}") : récupérer un client par son id.
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    //@Operation(summary = "Créer un nouveau client") : permet de documenter la route dans Swagger.
    @Operation(summary = "Créer un nouveau client")

    //@PostMapping : créer un nouveau client ou enregistrer un client dans la base de données.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO createClient(@Valid @RequestBody ClientDTO clientDTO) {
        return clientService.saveClient(clientDTO);
    }
    
    //@Operation(summary = "Mettre à jour un client existant") : permet de documenter la route dans Swagger.
    @Operation(summary = "Modifier un client")

    //@PutMapping : mettre à jour un enregistrement existant dans la base de données.
    @PutMapping("/{id}")
    public ClientDTO updateClient(@PathVariable Long id, 
        @Valid @RequestBody ClientDTO clientDTO) {
        return clientService.updateClient(id, clientDTO );
    }

    //@Operation(summary = "Supprimer un client existant") : permet de documenter la route dans Swagger.
    @Operation(summary = "Supprimer un client")

    //@DeleteMapping("/{id}") : supprimer un client par son id ou un enregistrement existant dans la base de données.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}