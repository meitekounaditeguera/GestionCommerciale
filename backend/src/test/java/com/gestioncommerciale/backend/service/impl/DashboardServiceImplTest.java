package com.gestioncommerciale.backend.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestioncommerciale.backend.model.Produit;
import com.gestioncommerciale.backend.repository.ClientRepository;
import com.gestioncommerciale.backend.repository.CommandeRepository;
import com.gestioncommerciale.backend.repository.LigneCommandeRepository;
import com.gestioncommerciale.backend.repository.MouvementStockRepository;
import com.gestioncommerciale.backend.repository.ProduitRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private LigneCommandeRepository ligneCommandeRepository;

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(
                clientRepository, produitRepository, commandeRepository,
                ligneCommandeRepository, mouvementStockRepository,
                DashboardServiceImpl.PERIODE_VELOCITE_JOURS_PAR_DEFAUT);
    }

    @Test
    void calculerVelociteVente_retourneZeroSiAucuneVenteRecente() {
        when(mouvementStockRepository.sumSortiesDepuis(eq(1L), any())).thenReturn(0L);

        double velocite = dashboardService.calculerVelociteVente(1L);

        assertThat(velocite).isZero();
    }

    @Test
    void calculerVelociteVente_retourneZeroSiLeRepositoryRenvoieNull() {
        // COALESCE côté SQL renvoie normalement 0, mais on couvre aussi le cas où l'agrégat
        // reviendrait null, pour ne jamais risquer de NullPointerException ici.
        when(mouvementStockRepository.sumSortiesDepuis(eq(1L), any())).thenReturn(null);

        double velocite = dashboardService.calculerVelociteVente(1L);

        assertThat(velocite).isZero();
    }

    @Test
    void calculerVelociteVente_calculeLaMoyenneJournaliereSurLaPeriode() {
        // 28 unités vendues sur 14 jours -> 2 unités/jour.
        when(mouvementStockRepository.sumSortiesDepuis(eq(1L), any())).thenReturn(28L);

        double velocite = dashboardService.calculerVelociteVente(1L);

        assertThat(velocite).isEqualTo(2.0);
    }

    @Test
    void calculerJoursAvantRupture_retourneIndetermineSiAucuneVenteRecente() {
        when(mouvementStockRepository.sumSortiesDepuis(eq(1L), any())).thenReturn(0L);

        Produit produit = new Produit();
        produit.setId(1L);
        produit.setQuantite(10);

        Optional<Integer> resultat = dashboardService.calculerJoursAvantRupture(produit);

        assertThat(resultat).isEmpty();
    }

    @Test
    void calculerJoursAvantRupture_estimeLeNombreDeJoursRestantsArrondiAuJourSuperieur() {
        // Vélocité de 2 unités/jour, stock de 9 -> 4,5 jours, arrondis à 5 : mieux vaut alerter
        // un peu trop tôt que de tomber en rupture avant le délai annoncé.
        when(mouvementStockRepository.sumSortiesDepuis(eq(1L), any())).thenReturn(28L);

        Produit produit = new Produit();
        produit.setId(1L);
        produit.setQuantite(9);

        Optional<Integer> resultat = dashboardService.calculerJoursAvantRupture(produit);

        assertThat(resultat).contains(5);
    }

    @Test
    void calculerVelociteVente_utiliseLaFenetreConfiguree() {
        // Fenêtre de 7 jours (dashboard.velocite.jours=7) : 28 unités -> 4 unités/jour, et les
        // ventes sont lues depuis il y a 7 jours.
        DashboardServiceImpl service7Jours = new DashboardServiceImpl(
                clientRepository, produitRepository, commandeRepository,
                ligneCommandeRepository, mouvementStockRepository, 7);
        when(mouvementStockRepository.sumSortiesDepuis(eq(1L), any())).thenReturn(28L);

        double velocite = service7Jours.calculerVelociteVente(1L);

        assertThat(velocite).isEqualTo(4.0);
        ArgumentCaptor<LocalDateTime> depuis = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(mouvementStockRepository).sumSortiesDepuis(eq(1L), depuis.capture());
        assertThat(depuis.getValue()).isCloseTo(LocalDateTime.now().minusDays(7), within(1, ChronoUnit.MINUTES));
    }

    @Test
    void constructeur_refuseUneFenetreDeVelociteInferieureAUnJour() {
        assertThatThrownBy(() -> new DashboardServiceImpl(
                clientRepository, produitRepository, commandeRepository,
                ligneCommandeRepository, mouvementStockRepository, 0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("VELOCITE_FENETRE_JOURS");
    }
}
