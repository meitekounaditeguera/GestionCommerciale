import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';


import { ClientService } from '../../services/client';
import { ProduitService } from '../../services/produit.service';
import { CommandeService } from '../../services/commande.service';
import { Client } from '../../models/client';
import { Produit } from '../../models/produit';
import { Commande } from '../../models/commande';

// Certains endpoints Spring Boot renvoient un tableau brut,
// d'autres une page paginée avec un champ "content".
type ReponseListe<T> = T[] | { content: T[] };

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {

  totalClients: number = 0;
  totalProduits: number = 0;
  totalCommandes: number = 0;
  chiffreAffaires: number = 0;
  isLoading: boolean = true; // Pratique pour afficher un loader sur la vue

  constructor(
    private clientService: ClientService,
    private produitService: ProduitService,
    private commandeService: CommandeService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    console.log('--- DASHBOARD CHARGÉ ---');
    this.chargerStatistiquesParallele();
  }

  /**
   * Option A : Chargement en parallèle avec RxJS forkJoin (Recommandé)
   * Exécute les 3 requêtes en simultané et attend le retour de toutes.
   */
 chargerStatistiquesParallele(): void {
  this.isLoading = true;

  forkJoin({
    clients: this.clientService.getClients(),
    produits: this.produitService.getProduits(),
    commandes: this.commandeService.getCommandes()
  }).subscribe({

    
    next: (res: {
      clients: ReponseListe<Client>;
      produits: ReponseListe<Produit>;
      commandes: ReponseListe<Commande>;
    }) => {

      const listClients = this.extraireListe(res.clients);
      this.totalClients = listClients.length;

      const listProduits = this.extraireListe(res.produits);
      this.totalProduits = listProduits.length;

      const listCommandes = this.extraireListe(res.commandes);
      this.totalCommandes = listCommandes.length;

      // Chiffre d'affaires
      this.chiffreAffaires = listCommandes.reduce(
        (total, c) => total + (c.montantTotal ?? 0),
        0
      );

      console.log('>>> VALEURS CALCULÉES :', {
        totalClients: this.totalClients,
        totalProduits: this.totalProduits,
        totalCommandes: this.totalCommandes,
        chiffreAffaires: this.chiffreAffaires
      });

      this.isLoading = false;
      this.cdr.detectChanges(); // Forcer la détection des changements après les mises à jour
    },
    error: (err) => {
      console.error('Erreur lors du chargement des données :', err);
      this.isLoading = false;

    }
  });
}

// Normalise un tableau brut ou une page Spring paginée en simple tableau.
private extraireListe<T>(reponse: ReponseListe<T>): T[] {
  return Array.isArray(reponse) ? reponse : reponse.content;
}

}