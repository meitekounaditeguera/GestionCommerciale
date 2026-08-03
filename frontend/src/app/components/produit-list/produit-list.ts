import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Produit } from '../../models/produit';
import { ProduitService } from '../../services/produit.service';

// Colonnes sur lesquelles le tri est autorisé.
type ColonneTriable = 'nom' | 'prix' | 'quantite';

@Component({
  selector: 'app-produit-list',
  standalone: true,

  imports: [

  CommonModule,
  FormsModule

],

  templateUrl: './produit-list.html',
  styleUrls: ['./produit-list.css']
})
export class ProduitListComponent implements OnInit {

  // Liste des produits
  produits: Produit[] = [];
  messageSucces: string = '';
  messageErreur: string = '';

// ===============================
// PAGINATION
// ===============================

// Page actuellement affichée.
pageCourante = 1;

// Nombre de produits affichés par page.
produitsParPage = 5;

  // Liste complète des produits.
tousLesProduits: Produit[] = [];

// Texte saisi dans la zone de recherche.
recherche = '';

  // Objet lié au formulaire d'ajout d'un produit.
nouveauProduit: Produit = {

  nom: '',
  description: '',
  prix: 0,
  quantite: 0

};

// Contient l'identifiant du produit en cours de modification.
// null signifie que le formulaire est en mode "Ajout".
produitEnModification: number | null = null;
  produitsFitres: Produit[] = [];

  constructor(private produitService: ProduitService) {}

  ngOnInit(): void {

    console.log("Chargement des produits...");

    this.produitService.getProduits().subscribe({

      next: (data) => {

        console.log(data);

        this.tousLesProduits = data;
        this.produits = data;
        this.produitsFitres = [...data];

      },

      error: (err) => {

        console.error("Erreur :", err);

      }

    });

  }

  ajouterProduit(): void {

  // Validation : le prix doit être strictement supérieur à 0.
  if (!this.nouveauProduit.prix || this.nouveauProduit.prix <= 0) {
    this.messageErreur = 'Le prix doit être supérieur à 0.';
    setTimeout(() => (this.messageErreur = ''), 3000);
    return;
  }

  // ===========================
  // MODE MODIFICATION
  // ===========================
  if (this.produitEnModification !== null) {

    this.produitService
      .updateProduit(
        this.produitEnModification,
        this.nouveauProduit
      )
      .subscribe({

        next: () => {

          // Recharge toute la liste depuis le backend.
          this.produitService.getProduits().subscribe(data => {

              this.tousLesProduits = data;
              this.produits = data;
              this.produitsFitres = [...data];

          });

          // Réinitialise le formulaire.
          this.nouveauProduit = {
            nom: '',
            description: '',
            prix: 0,
            quantite: 0
          };

          // Retour au mode Ajout.
          this.produitEnModification = null;

          // Message de succès pour modification
          this.messageSucces = 'Produit mis à jour avec succès !';
          setTimeout(() => (this.messageSucces = ''), 3000);

        },

        error: (err) => {
          console.error(err);
        }

      });

  }

  // ===========================
  // MODE AJOUT
  // ===========================
  else {

    this.produitService.addProduit(
      this.nouveauProduit
    ).subscribe({

      next: () => {

        // Recharge la liste.
        this.produitService.getProduits().subscribe(data => {

            this.tousLesProduits = data;
            this.produits = data;
            this.produitsFitres = [...data];

      });

        // Vide le formulaire.
        this.nouveauProduit = {
          nom: '',
          description: '',
          prix: 0,
          quantite: 0
        };

        // Message de succès pour ajout
        this.messageSucces = 'Produit ajouté avec succès !';
        setTimeout(() => (this.messageSucces = ''), 3000);
      },

      error: (err) => {
        console.error(err);
      }

    });

  }

}

// Charge les informations du produit dans le formulaire.
modifierProduit(produit: Produit): void {

  // On mémorise l'identifiant.
  this.produitEnModification = produit.id!;

  // On copie le produit dans le formulaire.
  this.nouveauProduit = { ...produit };

}

// Annuler une moficatiiion
annulerModification(): void {
  // 1. On vide le formulaire des produits
  this.nouveauProduit = {
    nom: '',
    description: '',
    prix: 0,
    quantite: 0
  };
  // 2. On repasse à null pour revenir au mode "Ajout"
  this.produitEnModification = null;
}

// Supprime un produit de la base de données.
supprimerProduit(id: number): void {
  // 1. Demande de confirmation préalable
  if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
    this.produitService.supprimerProduit(id).subscribe({
      next: () => {
        // 2. Message de succès
        alert('Le produit a été supprimé avec succès !');

        // 3. Mise à jour immédiate de la liste en mémoire (évite le double clic/rechargement)
        this.produits = this.produits.filter(p => p.id !== id);
        this.produitsFitres = this.produitsFitres.filter(p => p.id !== id);
        this.tousLesProduits = this.tousLesProduits.filter(p => p.id !== id);
      },
      error: (err) => {
        console.error('Erreur lors de la suppression du produit :', err);
        alert('Impossible de supprimer ce produit.');
      }
    });
  }
}

// Recherche des produits par nom, en temps réel.
rechercherProduit(): void {

  const valeur = this.recherche.toLowerCase().trim();

  this.produits = this.tousLesProduits.filter(produit =>

    produit.nom.toLowerCase().includes(valeur)

  );

  // Retour à la première page pour éviter une pagination hors limites.
  this.pageCourante = 1;

}

// Retourne le nombre total de pages.
get nombrePages(): number {

  return Math.ceil(
    this.produits.length / this.produitsParPage
  );

}

// Retourne uniquement les produits
// de la page actuellement affichée.
get produitsPagine(): Produit[] {

  const debut =
    (this.pageCourante - 1) * this.produitsParPage;

  return this.produits.slice(

    debut,

    debut + this.produitsParPage

  );

}

// Passe à la page suivante.
pageSuivante(): void {

  if (this.pageCourante < this.nombrePages) {

    this.pageCourante++;

  }

}

// Revient à la page précédente.
pagePrecedente(): void {

  if (this.pageCourante > 1) {

    this.pageCourante--;

  }

}

// ===============================
// TRI
// ===============================

// Colonne actuellement triée.
colonneTri: ColonneTriable = 'nom';

// Sens du tri.
// true = ordre croissant
// false = ordre décroissant
ordreCroissant = true;

// Trie les produits selon la colonne sélectionnée.
trier(colonne: ColonneTriable): void {

  // Si on clique deux fois sur la même colonne,
  // on inverse le sens du tri.
  if (this.colonneTri === colonne) {

    this.ordreCroissant = !this.ordreCroissant;

  } else {

    this.colonneTri = colonne;
    this.ordreCroissant = true;

  }

  this.produits.sort((a, b) => {

    if (a[colonne] < b[colonne]) {
      return this.ordreCroissant ? -1 : 1;
    }

    if (a[colonne] > b[colonne]) {
      return this.ordreCroissant ? 1 : -1;
    }

    return 0;

  });

}

}