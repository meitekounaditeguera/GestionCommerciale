package com.gestioncommerciale.backend.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
import com.gestioncommerciale.backend.model.Produit;
import com.gestioncommerciale.backend.repository.ClientRepository;
import com.gestioncommerciale.backend.repository.CommandeRepository;
import com.gestioncommerciale.backend.repository.LigneCommandeRepository;
import com.gestioncommerciale.backend.repository.ProduitRepository;
import com.gestioncommerciale.backend.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    // Nombre de mois affichés dans l'évolution du chiffre d'affaires.
    private static final int MOIS_HISTORIQUE = 12;

    // Seuil fixe en dessous duquel un produit est considéré en rupture / stock bas.
    private static final int SEUIL_STOCK_BAS = 9;

    // Nombre maximal de produits retournés par le classement des ventes.
    private static final int TOP_PRODUITS_LIMIT = 10;

    // Période (en jours) sur laquelle un client est considéré comme "nouveau".
    private static final int PERIODE_NOUVEAUX_CLIENTS_JOURS = 30;

    private static final DateTimeFormatter LIBELLE_MOIS = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);

    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;

    public DashboardServiceImpl(ClientRepository clientRepository,
            ProduitRepository produitRepository,
            CommandeRepository commandeRepository,
            LigneCommandeRepository ligneCommandeRepository) {
        this.clientRepository = clientRepository;
        this.produitRepository = produitRepository;
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
    }

    @Override
    public DashboardStatsDTO getStats() {
        long totalClients = clientRepository.count();
        long totalProduits = produitRepository.count();
        long totalCommandes = commandeRepository.count();
        return new DashboardStatsDTO(
                totalClients,
                totalProduits,
                totalCommandes,
                commandeRepository.sumMontantTotal());
    }

    @Override
    public List<CaMensuelDTO> getCaMensuel() {
        YearMonth premierMois = YearMonth.now().minusMonths(MOIS_HISTORIQUE - 1L);

        Map<String, BigDecimal> totauxParMois = new HashMap<>();
        for (Object[] ligne : commandeRepository.sumMontantParMoisDepuis(premierMois.atDay(1))) {
            totauxParMois.put((String) ligne[0], (BigDecimal) ligne[1]);
        }

        List<CaMensuelDTO> resultat = new ArrayList<>();
        for (int i = 0; i < MOIS_HISTORIQUE; i++) {
            YearMonth mois = premierMois.plusMonths(i);
            BigDecimal total = totauxParMois.getOrDefault(mois.toString(), BigDecimal.ZERO);
            resultat.add(new CaMensuelDTO(mois.atDay(1).format(LIBELLE_MOIS), total));
        }
        return resultat;
    }

    @Override
    public List<VentesParCategorieDTO> getVentesParCategorie() {
        List<VentesParCategorieDTO> resultat = new ArrayList<>();
        for (Object[] ligne : ligneCommandeRepository.sumVentesParCategorie()) {
            resultat.add(new VentesParCategorieDTO((String) ligne[0], (BigDecimal) ligne[1]));
        }
        return resultat;
    }

    @Override
    public List<TopProduitDTO> getTopProduits() {
        List<TopProduitDTO> resultat = new ArrayList<>();
        for (Object[] ligne : ligneCommandeRepository.findTopProduits(PageRequest.of(0, TOP_PRODUITS_LIMIT))) {
            resultat.add(new TopProduitDTO((Long) ligne[0], (String) ligne[1], (Long) ligne[2], (BigDecimal) ligne[3]));
        }
        return resultat;
    }

    @Override
    public List<ProduitRuptureDTO> getRupturesStock() {
        List<ProduitRuptureDTO> resultat = new ArrayList<>();
        for (Produit produit : produitRepository.findByActifTrueAndQuantiteLessThanEqualOrderByQuantiteAsc(SEUIL_STOCK_BAS)) {
            resultat.add(new ProduitRuptureDTO(produit.getId(), produit.getNom(), produit.getQuantite()));
        }
        return resultat;
    }

    @Override
    public NouveauxClientsDTO getNouveauxClients() {
        LocalDate depuis = LocalDate.now().minusDays(PERIODE_NOUVEAUX_CLIENTS_JOURS);
        long nombre = clientRepository.countByDateCreationGreaterThanEqual(depuis);
        return new NouveauxClientsDTO(nombre, PERIODE_NOUVEAUX_CLIENTS_JOURS);
    }

    @Override
    public ChiffreAffairesDTO getChiffreAffaires() {

        LocalDate aujourdHui = LocalDate.now();
        LocalDate debutSemaine = aujourdHui.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);
        LocalDate debutAnnee = aujourdHui.withDayOfYear(1);

        return new ChiffreAffairesDTO(
                commandeRepository.sumMontantTotalDepuis(aujourdHui),
                commandeRepository.sumMontantTotalDepuis(debutSemaine),
                commandeRepository.sumMontantTotalDepuis(debutMois),
                commandeRepository.sumMontantTotalDepuis(debutAnnee));
    }

    @Override
    public MeilleurClientDTO getMeilleurClient() {

        List<Object[]> resultats = commandeRepository.findMeilleursClients(PageRequest.of(0, 1));

        if (resultats.isEmpty()) {
            return null;
        }

        Object[] ligne = resultats.get(0);
        return new MeilleurClientDTO((String) ligne[1], (String) ligne[2], (BigDecimal) ligne[3]);
    }

    @Override
    public ProduitPhareDTO getProduitPhare() {

        List<Object[]> resultats = ligneCommandeRepository.findTopProduits(PageRequest.of(0, 1));

        if (resultats.isEmpty()) {
            return null;
        }

        Object[] ligne = resultats.get(0);
        return new ProduitPhareDTO((String) ligne[1], (Long) ligne[2]);
    }

    @Override
    public CategoriePopulaireDTO getCategoriePopulaire() {

        List<Object[]> resultats = ligneCommandeRepository.sumQuantiteParCategorie();

        if (resultats.isEmpty()) {
            return null;
        }

        Object[] ligne = resultats.get(0);
        return new CategoriePopulaireDTO((String) ligne[0], (Long) ligne[1]);
    }
}
