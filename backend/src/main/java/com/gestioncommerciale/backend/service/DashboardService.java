package com.gestioncommerciale.backend.service;

import java.util.List;

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

public interface DashboardService {

    DashboardStatsDTO getStats();

    List<CaMensuelDTO> getCaMensuel();

    List<VentesParCategorieDTO> getVentesParCategorie();

    List<TopProduitDTO> getTopProduits();

    List<ProduitRuptureDTO> getRupturesStock();

    NouveauxClientsDTO getNouveauxClients();

    // Chiffre d'affaires cumulé : aujourd'hui, semaine en cours, mois en cours, année en cours.
    ChiffreAffairesDTO getChiffreAffaires();

    // Client ayant généré le plus de chiffre d'affaires (null si aucune commande).
    MeilleurClientDTO getMeilleurClient();

    // Produit le plus vendu en quantité (null si aucune vente).
    ProduitPhareDTO getProduitPhare();

    // Catégorie de produit la plus vendue en quantité (null si aucune vente).
    CategoriePopulaireDTO getCategoriePopulaire();

}
