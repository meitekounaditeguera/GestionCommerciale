import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CommandeService } from '../../services/commande.service';
import { Commande } from '../../models/commande';
import { Client } from '../../models/client';
import { ClientService } from '../../services/client';
import { Produit } from '../../models/produit';
import { ProduitService } from '../../services/produit.service';
import { LigneCommande } from '../../models/lignecommande';


@Component({
  selector: 'app-commande-list',
  
  imports: [
  CommonModule,
  FormsModule
],

  templateUrl: './commande-list.html',
  styleUrl: './commande-list.css',
})

export class CommandeListComponent {

clients: Client[] = [];  
commandes: Commande[] = [];
produits: Produit[] = [];
messageSucces = '';

nouvelleCommande: Commande = {
  dateCommande: '',
  clientId: 0,
  lignes: []
};

nouvelleLigne: LigneCommande = {
  produitId: 0,
  quantite: 1
};

constructor(
  private commandeService: CommandeService,
  private clientService: ClientService,
  private produitService: ProduitService
) {}

ngOnInit(): void {

  this.chargerCommandes();
  this.chargerClients();
  this.chargerProduits();

}

chargerCommandes(): void {

  this.commandeService.getCommandes().subscribe({

    next: (data) => {
      this.commandes = data;
    },

    error: (err) => {
      console.error(err);
    }

  });

}

ajouterCommande(): void {

  
  this.commandeService.addCommande(this.nouvelleCommande).subscribe({

    next: (commande) => {

  this.messageSucces = '✅ Commande enregistrée avec succès !';
  setTimeout(() => (this.messageSucces = ''), 3000);

  this.nouvelleCommande = {
    dateCommande: '',
    clientId: 0,
    lignes: []
  };

  this.chargerCommandes();

},

    error: (err) => {
      console.error(err);
    }

  });

}

chargerClients(): void {

  this.clientService.getClients().subscribe({

    next: (data) => {
      this.clients = data;
    },

    error: (err) => {
      console.error(err);
    }

  });

}

chargerProduits(): void {

  this.produitService.getProduits().subscribe({

    next: (data) => {
      this.produits = data;
    },

    error: (err) => {
      console.error(err);
    }

  });

}

ajouterLigne(): void {

  this.nouvelleCommande.lignes.push({

    produitId: this.nouvelleLigne.produitId,
    quantite: this.nouvelleLigne.quantite

  });

  // Réinitialiser la ligne
  this.nouvelleLigne = {

    produitId: 0,
    quantite: 1

  };

}

supprimerLigne(index: number): void {

  if (confirm('Voulez-vous vraiment retirer ce produit de la commande ?')) {
    this.nouvelleCommande.lignes.splice(index, 1);
  }

}

//Afficher le nom du produit au lieu de son ID
getNomProduit(produitId: number): string {

  const produit = this.produits.find(
    p => p.id === produitId
  );

  return produit ? produit.nom : "Produit inconnu";

}

//La méthode pour récupérer le prix d'un produit
getPrixProduit(produitId: number): number {

  const produit = this.produits.find(
    p => p.id === produitId
  );

  return produit ? produit.prix : 0;

}

//Calculer le montant total automatiquement
calculerMontantTotal(): number {

  return this.nouvelleCommande.lignes.reduce((total, ligne) => {

    return total + (
      this.getPrixProduit(ligne.produitId) * ligne.quantite
    );

  }, 0);

}

}
