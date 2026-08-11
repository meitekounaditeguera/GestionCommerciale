import { Component } from '@angular/core';

import { NavbarComponent } from '../layout/navbar/navbar';
import { DashboardComponent } from '../dashboard/dashboard';
import { ClientListComponent } from '../client-list/client-list';
import { ProduitListComponent } from '../produit-list/produit-list';
import { CommandeListComponent } from '../commande-list/commande-list';
import { HistoriqueStockComponent } from '../historique-stock/historique-stock';
import { FournisseurListComponent } from '../fournisseur-list/fournisseur-list';
import { CommandeFournisseurComponent } from '../commande-fournisseur/commande-fournisseur';
import { AuditLogsComponent } from '../audit-logs/audit-logs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  // Composants utilisés pour construire la page d’accueil principale.
  imports: [
    NavbarComponent,
    DashboardComponent,
    ClientListComponent,
    ProduitListComponent,
    CommandeListComponent,
    HistoriqueStockComponent,
    FournisseurListComponent,
    CommandeFournisseurComponent,
    AuditLogsComponent
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
// Page d’accueil principale de l’application.
export class HomeComponent {
  constructor(public authService: AuthService) {}
}
