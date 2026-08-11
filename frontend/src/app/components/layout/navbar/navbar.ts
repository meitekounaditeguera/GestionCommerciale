import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';

import { AuthService } from '../../../services/auth.service';
import { ProduitService } from '../../../services/produit.service';
import { DataRefreshService } from '../../../services/data-refresh.service';
import { ThemeService, Theme } from '../../../services/theme.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class NavbarComponent implements OnInit, OnDestroy {

  // Nombre de produits en rupture de stock (quantité == 0), affiché en badge sur l'onglet Stock.
  produitsEnRupture: number = 0;

  // Thème courant, tenu à jour via l'abonnement à ThemeService pour piloter l'icône du bouton.
  theme: Theme = 'light';

  private refreshSubscription?: Subscription;
  private themeSubscription?: Subscription;

  constructor(
    public authService: AuthService,
    private router: Router,
    private produitService: ProduitService,
    private dataRefreshService: DataRefreshService,
    private themeService: ThemeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.themeSubscription = this.themeService.theme$.subscribe(theme => {
      this.theme = theme;
      this.cdr.markForCheck();
    });

    if (this.authService.hasAnyRole(['ROLE_ADMIN', 'ROLE_GESTIONNAIRE'])) {
      this.chargerProduitsEnRupture();

      this.refreshSubscription = this.dataRefreshService.dataChanged$.subscribe(() => {
        this.chargerProduitsEnRupture();
      });
    }
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
    this.themeSubscription?.unsubscribe();
  }

  // Bascule instantanément entre mode clair et mode sombre (aucun rechargement de page).
  basculerTheme(): void {
    this.themeService.basculerTheme();
  }

  // Demande l'ensemble du catalogue (pas les 5 éléments de la pagination) pour compter
  // précisément les produits dont le stock est à 0.
  private chargerProduitsEnRupture(): void {
    this.produitService.getProduits(0, 1000).subscribe({
      next: (reponse) => {
        this.produitsEnRupture = (reponse?.content ?? []).filter(p => p.quantite === 0).length;
        this.cdr.markForCheck();
      },
      error: () => {
        this.produitsEnRupture = 0;
        this.cdr.markForCheck();
      }
    });
  }

  // Supprime le token de session et renvoie l'utilisateur vers la page de connexion.
  deconnexion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
