import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

import { DashboardService } from '../../services/dashboard.service';
import { DataRefreshService } from '../../services/data-refresh.service';
import { ThemeService, Theme } from '../../services/theme.service';
import {
  CaMensuel,
  VentesParCategorie,
  TopProduit,
  ProduitRupture,
  ChiffreAffaires,
  MeilleurClient,
  ProduitPhare,
  CategoriePopulaire
} from '../../models/dashboard-stats';

// Palette catégorielle (ordre fixe, ne jamais recycler les teintes)
const COULEURS_CATEGORIELLES = [
  '#2a78d6', '#eb6834', '#1baf7a', '#eda100',
  '#e87ba4', '#008300', '#4a3aa7', '#e34948'
];
const COULEUR_SEQUENTIELLE = '#2a78d6';
const COULEUR_SEQUENTIELLE_FOND = 'rgba(42, 120, 214, 0.15)';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit, OnDestroy {

  totalClients: number = 0;
  totalProduits: number = 0;
  totalCommandes: number = 0;
  chiffreAffaires: number = 0;
  isLoading: boolean = true; // Pratique pour afficher un loader sur la vue
  private refreshSubscription?: Subscription;
  private themeSubscription?: Subscription;

  // Thème courant : pilote les couleurs (texte des axes, grille, bordures des segments)
  // des graphiques Chart.js, qui ne suivent pas automatiquement data-bs-theme (rendu canvas).
  private themeActuel: Theme = 'light';

  // Dernier jeu de données brut reçu pour chaque graphique, conservé pour pouvoir les
  // redessiner avec les nouvelles couleurs dès que l'utilisateur bascule de thème.
  private donneesCaMensuel: CaMensuel[] = [];
  private donneesVentesParCategorie: VentesParCategorie[] = [];
  private donneesTopProduits: TopProduit[] = [];

  // Nouveaux clients (badge)
  nouveauxClients: number = 0;
  periodeNouveauxClientsJours: number = 30;

  // Ruptures / stock bas
  rupturesStock: ProduitRupture[] = [];

  // Chiffre d'affaires cumulé par période.
  chiffreAffairesResume: ChiffreAffaires = { journalier: 0, hebdomadaire: 0, mensuel: 0, annuel: 0 };

  // Top KPIs. null = donnée pas encore chargée ou aucune vente enregistrée.
  meilleurClient: MeilleurClient | null = null;
  produitPhare: ProduitPhare | null = null;
  categoriePopulaire: CategoriePopulaire | null = null;

  // Indicateurs d'absence de données (pour ne pas planter l'affichage)
  caMensuelVide: boolean = false;
  ventesCategorieVide: boolean = false;
  topProduitsVide: boolean = false;

  // Tendance du CA du mois en cours par rapport au mois précédent (null = pas assez
  // de données, ex: moins de 2 mois d'historique ou mois précédent à 0 FCFA).
  tendanceCA: { pourcentage: number; hausse: boolean } | null = null;

  // ================= Graphique en ligne : CA mensuel =================
  lineChartData: ChartData<'line'> = { labels: [], datasets: [] };
  lineChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      y: { beginAtZero: true }
    }
  };

  // ================= Graphique doughnut : ventes par catégorie =================
  doughnutChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom' }
    }
  };

  // ================= Graphique en barres : top 10 produits =================
  barChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y',
    plugins: {
      legend: { display: false }
    },
    scales: {
      x: { beginAtZero: true }
    }
  };

  constructor(
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef,
    private dataRefreshService: DataRefreshService,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    // Redessine les 3 graphiques avec les couleurs du thème courant à chaque changement
    // (y compris la valeur initiale, émise immédiatement par ce BehaviorSubject).
    this.themeSubscription = this.themeService.theme$.subscribe(theme => {
      this.themeActuel = theme;
      this.construireLineChart(this.donneesCaMensuel);
      this.construireDoughnutChart(this.donneesVentesParCategorie);
      this.construireBarChart(this.donneesTopProduits);
    });

    this.chargerToutesLesDonnees();

    this.refreshSubscription = this.dataRefreshService.dataChanged$.subscribe(() => {
      this.chargerToutesLesDonnees();
    });
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
    this.themeSubscription?.unsubscribe();
  }

  // Charge les statistiques agrégées et les 5 jeux de données des graphiques.
  // Chaque appel est isolé : l'échec d'un endpoint n'empêche pas l'affichage des autres.
  chargerToutesLesDonnees(): void {
    this.isLoading = true;

    this.dashboardService.getStats().subscribe({
      next: (stats) => {
        this.totalClients = stats.totalClients;
        this.totalProduits = stats.totalProduits;
        this.totalCommandes = stats.totalCommandes;
        this.chiffreAffaires = stats.chiffreAffaires;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /stats :', err.error ?? err.message);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });

    this.dashboardService.getCaMensuel().subscribe({
      next: (data) => this.construireLineChart(data ?? []),
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /ca-mensuel :', err.error ?? err.message);
        this.construireLineChart([]);
      }
    });

    this.dashboardService.getVentesParCategorie().subscribe({
      next: (data) => this.construireDoughnutChart(data ?? []),
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /ventes-par-categorie :', err.error ?? err.message);
        this.construireDoughnutChart([]);
      }
    });

    this.dashboardService.getTopProduits().subscribe({
      next: (data) => this.construireBarChart(data ?? []),
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /top-produits :', err.error ?? err.message);
        this.construireBarChart([]);
      }
    });

    this.dashboardService.getRupturesStock().subscribe({
      next: (data) => {
        // Tri croissant par quantité : les alertes critiques (rouge) remontent en premier.
        this.rupturesStock = (data ?? []).slice().sort((a, b) => a.quantite - b.quantite);
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /ruptures-stock :', err.error ?? err.message);
        this.rupturesStock = [];
        this.cdr.detectChanges();
      }
    });

    this.dashboardService.getNouveauxClients().subscribe({
      next: (data) => {
        this.nouveauxClients = data?.nombre ?? 0;
        this.periodeNouveauxClientsJours = data?.periodeJours ?? 30;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /nouveaux-clients :', err.error ?? err.message);
        this.nouveauxClients = 0;
        this.cdr.detectChanges();
      }
    });

    this.dashboardService.getChiffreAffaires().subscribe({
      next: (data) => {
        this.chiffreAffairesResume = data ?? { journalier: 0, hebdomadaire: 0, mensuel: 0, annuel: 0 };
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /ca :', err.error ?? err.message);
        this.chiffreAffairesResume = { journalier: 0, hebdomadaire: 0, mensuel: 0, annuel: 0 };
        this.cdr.detectChanges();
      }
    });

    this.dashboardService.getMeilleurClient().subscribe({
      next: (data) => {
        this.meilleurClient = data ?? null;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /meilleur-client :', err.error ?? err.message);
        this.meilleurClient = null;
        this.cdr.detectChanges();
      }
    });

    this.dashboardService.getProduitPhare().subscribe({
      next: (data) => {
        this.produitPhare = data ?? null;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /produit-phare :', err.error ?? err.message);
        this.produitPhare = null;
        this.cdr.detectChanges();
      }
    });

    this.dashboardService.getCategoriePopulaire().subscribe({
      next: (data) => {
        this.categoriePopulaire = data ?? null;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('[Dashboard] Échec /categorie-populaire :', err.error ?? err.message);
        this.categoriePopulaire = null;
        this.cdr.detectChanges();
      }
    });
  }

  // Rupture imminente (rouge) : quantité entre 0 et 4. Stock bas (orange) : quantité entre 5 et 9.
  estRuptureImminente(quantite: number): boolean {
    return quantite < 5;
  }

  // Estimation grossière du délai avant rupture totale, à partir du seuil de quantité :
  // une règle simple pour donner un ordre de grandeur au gestionnaire, pas une prévision
  // fondée sur la vitesse de vente réelle du produit.
  estimationEpuisement(quantite: number): string {
    if (quantite === 0) {
      return 'Stock déjà épuisé';
    }
    if (quantite < 5) {
      return 'Épuisement estimé : ~3 jours';
    }
    return 'Épuisement estimé : ~1 semaine';
  }

  // Compare le CA du mois en cours à celui du mois précédent (les deux derniers points
  // de la série /ca-mensuel, déjà triée chronologiquement par le backend).
  private calculerTendanceCA(data: CaMensuel[]): void {

    if (data.length < 2) {
      this.tendanceCA = null;
      return;
    }

    const moisActuel = data[data.length - 1].chiffreAffaires;
    const moisPrecedent = data[data.length - 2].chiffreAffaires;

    if (!moisPrecedent) {
      this.tendanceCA = null;
      return;
    }

    const pourcentage = ((moisActuel - moisPrecedent) / moisPrecedent) * 100;
    this.tendanceCA = { pourcentage, hausse: pourcentage >= 0 };
  }

  // Couleurs des éléments Chart.js sensibles au thème (texte des axes/légendes, grille,
  // bordure des segments du doughnut) : le canvas ne suit pas data-bs-theme tout seul.
  private couleursGraphique(): { texte: string; grille: string; bordureSegment: string } {
    return this.themeActuel === 'dark'
      ? { texte: '#ced4da', grille: 'rgba(255, 255, 255, 0.12)', bordureSegment: '#2b3035' }
      : { texte: '#495057', grille: 'rgba(0, 0, 0, 0.08)', bordureSegment: '#fcfcfb' };
  }

  private construireLineChart(data: CaMensuel[]): void {
    this.donneesCaMensuel = data;
    this.caMensuelVide = data.length === 0 || data.every(d => !d.chiffreAffaires);
    this.calculerTendanceCA(data);

    const { texte, grille } = this.couleursGraphique();

    this.lineChartData = {
      labels: data.map(d => d.mois),
      datasets: [{
        label: "Chiffre d'affaires",
        data: data.map(d => d.chiffreAffaires),
        borderColor: COULEUR_SEQUENTIELLE,
        backgroundColor: COULEUR_SEQUENTIELLE_FOND,
        fill: true,
        tension: 0.3,
        pointRadius: 3,
        pointBackgroundColor: COULEUR_SEQUENTIELLE
      }]
    };
    this.lineChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        x: { ticks: { color: texte }, grid: { color: grille } },
        y: { beginAtZero: true, ticks: { color: texte }, grid: { color: grille } }
      }
    };
    this.cdr.detectChanges();
  }

  private construireDoughnutChart(data: VentesParCategorie[]): void {
    this.donneesVentesParCategorie = data;
    this.ventesCategorieVide = data.length === 0;

    const { texte, bordureSegment } = this.couleursGraphique();

    this.doughnutChartData = {
      labels: data.map(d => d.categorie),
      datasets: [{
        data: data.map(d => d.totalVentes),
        backgroundColor: data.map((_, i) => COULEURS_CATEGORIELLES[i % COULEURS_CATEGORIELLES.length]),
        borderWidth: 2,
        borderColor: bordureSegment
      }]
    };
    this.doughnutChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'bottom', labels: { color: texte } }
      }
    };
    this.cdr.detectChanges();
  }

  private construireBarChart(data: TopProduit[]): void {
    this.donneesTopProduits = data;
    this.topProduitsVide = data.length === 0;

    const { texte, grille } = this.couleursGraphique();

    this.barChartData = {
      labels: data.map(d => d.nom),
      datasets: [{
        label: 'Quantité vendue',
        data: data.map(d => d.quantiteVendue),
        backgroundColor: COULEUR_SEQUENTIELLE
      }]
    };
    this.barChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      indexAxis: 'y',
      plugins: {
        legend: { display: false }
      },
      scales: {
        x: { beginAtZero: true, ticks: { color: texte }, grid: { color: grille } },
        y: { ticks: { color: texte }, grid: { color: grille } }
      }
    };
    this.cdr.detectChanges();
  }
}
