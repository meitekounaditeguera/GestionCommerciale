package com.gestioncommerciale.backend.controller;

import com.gestioncommerciale.backend.dto.ProduitDTO;
import com.gestioncommerciale.backend.service.ProduitService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:4200")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    // Retourne tous les produits
    @GetMapping
    public List<ProduitDTO> getAllProduits() {
        return produitService.getAllProduits();
    }

    // Retourne un produit par son id
    @GetMapping("/{id}")
    public ProduitDTO getProduitById(@PathVariable Long id) {
        return produitService.getProduitById(id);
    }

    // Ajoute un produit
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProduitDTO createProduit(@Valid @RequestBody ProduitDTO produitDTO) {
        return produitService.saveProduit(produitDTO);
    }

    // Modifie un produit
    @PutMapping("/{id}")
    public ProduitDTO updateProduit(
            @PathVariable Long id,
            @Valid @RequestBody ProduitDTO produitDTO) {

        return produitService.updateProduit(id, produitDTO);

    }

    // Supprime un produit
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduit(@PathVariable Long id) {

        produitService.deleteProduit(id);

    }

}