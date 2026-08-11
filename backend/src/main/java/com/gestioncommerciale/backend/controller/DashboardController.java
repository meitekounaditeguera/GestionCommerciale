package com.gestioncommerciale.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.gestioncommerciale.backend.dto.CaMensuelDTO;
import com.gestioncommerciale.backend.dto.CategoriePopulaireDTO;
import com.gestioncommerciale.backend.dto.ChiffreAffairesDTO;
import com.gestioncommerciale.backend.dto.DashboardStatsDTO;
import com.gestioncommerciale.backend.dto.MeilleurClientDTO;
import com.gestioncommerciale.backend.dto.NouveauxClientsDTO;
import com.gestioncommerciale.backend.dto.ProduitPhareDTO;
import com.gestioncommerciale.backend.dto.ProduitRuptureDTO;
import com.gestioncommerciale.backend.dto.TopProduitDTO;
import com.gestioncommerciale.backend.dto.VentesParCategorieDTO;
import com.gestioncommerciale.backend.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Tableau de bord",
    description = "API fournissant les statistiques agrégées pour le tableau de bord"
)
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Récupérer les statistiques du tableau de bord")
    @GetMapping("/stats")
    public DashboardStatsDTO getStats() {
        return dashboardService.getStats();
    }

    @Operation(summary = "Évolution du chiffre d'affaires par mois (12 derniers mois)")
    @GetMapping("/stats/ca-mensuel")
    public List<CaMensuelDTO> getCaMensuel() {
        return dashboardService.getCaMensuel();
    }

    @Operation(summary = "Ventes ventilées par catégorie de produit")
    @GetMapping("/stats/ventes-par-categorie")
    public List<VentesParCategorieDTO> getVentesParCategorie() {
        return dashboardService.getVentesParCategorie();
    }

    @Operation(summary = "Top 10 des produits les plus vendus")
    @GetMapping("/stats/top-produits")
    public List<TopProduitDTO> getTopProduits() {
        return dashboardService.getTopProduits();
    }

    @Operation(summary = "Produits en rupture ou stock bas (quantité <= 5)")
    @GetMapping("/stats/ruptures-stock")
    public List<ProduitRuptureDTO> getRupturesStock() {
        return dashboardService.getRupturesStock();
    }

    @Operation(summary = "Nombre de nouveaux clients enregistrés sur les 30 derniers jours")
    @GetMapping("/stats/nouveaux-clients")
    public NouveauxClientsDTO getNouveauxClients() {
        return dashboardService.getNouveauxClients();
    }

    @Operation(summary = "Chiffre d'affaires cumulé : jour, semaine, mois et année en cours")
    @GetMapping("/ca")
    public ChiffreAffairesDTO getChiffreAffaires() {
        return dashboardService.getChiffreAffaires();
    }

    @Operation(summary = "Client ayant généré le plus de chiffre d'affaires")
    @GetMapping("/meilleur-client")
    public MeilleurClientDTO getMeilleurClient() {
        return dashboardService.getMeilleurClient();
    }

    @Operation(summary = "Produit le plus vendu en quantité")
    @GetMapping("/produit-phare")
    public ProduitPhareDTO getProduitPhare() {
        return dashboardService.getProduitPhare();
    }

    @Operation(summary = "Catégorie de produit la plus vendue en quantité")
    @GetMapping("/categorie-populaire")
    public CategoriePopulaireDTO getCategoriePopulaire() {
        return dashboardService.getCategoriePopulaire();
    }
}
