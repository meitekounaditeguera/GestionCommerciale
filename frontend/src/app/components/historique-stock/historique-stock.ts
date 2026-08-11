import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';

import { StockService } from '../../services/stock.service';
import { MouvementStock } from '../../models/mouvement-stock';
import { Page } from '../../models/page';
import { DataRefreshService } from '../../services/data-refresh.service';

@Component({
  selector: 'app-historique-stock',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './historique-stock.html',
  styleUrls: ['./historique-stock.css']
})
export class HistoriqueStockComponent implements OnInit, OnDestroy {

  // Signals plutôt que des propriétés de classe mutées directement : la vue se met à jour
  // à chaque .set()/.update(), indépendamment de Zone.js. Élimine toute la classe de bugs
  // "la donnée change en mémoire mais la vue ne suit pas".
  readonly mouvements = signal<MouvementStock[]>([]);
  readonly pageCourante = signal(0);
  readonly totalPages = signal(0);
  readonly isLoading = signal(true);
  readonly erreurChargement = signal('');

  // Nombre de mouvements par page : fixé à 5 pour tous les appels HTTP.
  private readonly tailleDePage = 5;
  private refreshSubscription?: Subscription;

  constructor(
    private stockService: StockService,
    private dataRefreshService: DataRefreshService
  ) {}

  ngOnInit(): void {
    this.chargerHistorique();

    this.refreshSubscription = this.dataRefreshService.dataChanged$.subscribe(() => {
      this.chargerHistorique(this.pageCourante());
    });
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
  }

  chargerHistorique(page: number = 0): void {
    this.isLoading.set(true);
    this.erreurChargement.set('');

    console.log('[HistoriqueStock] Appel GET /api/stock/historique...', { page, taille: this.tailleDePage });

    this.stockService.getHistorique(page, this.tailleDePage).subscribe({
      next: (resultat: Page<MouvementStock> | MouvementStock[]) => {
        console.log("[HistoriqueStock] Réponse reçue de l'historique :", resultat);

        // L'API renvoie un objet paginé Spring (PageImpl, propriété "content").
        // On accepte aussi un tableau brut par sécurité, au cas où la forme de la réponse change.
        if (Array.isArray(resultat)) {
          this.mouvements.set(resultat);
          this.totalPages.set(1);
          this.pageCourante.set(0);
        } else {
          // Ne jamais recalculer totalPages manuellement : resultat.content ne contient
          // que les éléments de la page courante, pas le total.
          this.mouvements.set(resultat.content ?? []);
          this.totalPages.set(resultat.totalPages > 0 ? resultat.totalPages : 1);
          this.pageCourante.set(resultat.number ?? 0);
        }

        this.isLoading.set(false);

        console.log('[HistoriqueStock] mouvements après extraction :', this.mouvements());
      },
      error: (err: HttpErrorResponse) => {
        console.error("[HistoriqueStock] Échec du chargement de l'historique :", err.status, err.error ?? err.message);

        this.mouvements.set([]);
        this.isLoading.set(false);

        // Un message explicite plutôt qu'un tableau vide indistinguable d'un "aucune donnée".
        this.erreurChargement.set(
          err.status === 403
            ? "Vous n'avez pas les droits nécessaires pour consulter l'historique du stock."
            : "Impossible de charger l'historique du stock. Réessayez plus tard."
        );
      }
    });
  }

  pagePrecedente(): void {
    if (this.pageCourante() > 0) {
      this.chargerHistorique(this.pageCourante() - 1);
    }
  }

  pageSuivante(): void {
    if (this.pageCourante() < this.totalPages() - 1) {
      this.chargerHistorique(this.pageCourante() + 1);
    }
  }
}
