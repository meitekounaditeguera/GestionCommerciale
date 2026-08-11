package com.gestioncommerciale.backend.controller;

import com.gestioncommerciale.backend.dto.ProduitDTO;
import com.gestioncommerciale.backend.service.ProduitService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:4200")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    // Retourne les produits de la page demandée.
    @GetMapping
    public Page<ProduitDTO> getAllProduits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return produitService.getAllProduits(pageable);
    }

    // Retourne un produit par son id
    @GetMapping("/{id}")
    public ProduitDTO getProduitById(@PathVariable Long id) {
        return produitService.getProduitById(id);
    }

    // Retourne le produit correspondant à un code-barres/QR code scanné.
    @GetMapping("/recherche/code-barre")
    public ProduitDTO getProduitByCodeBarre(@RequestParam String code) {
        return produitService.getProduitByCodeBarre(code);
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