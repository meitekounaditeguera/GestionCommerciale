import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Produit } from '../../models/produit';
import { ProduitService } from '../../services/produit.service';
import { DataRefreshService } from '../../services/data-refresh.service';
import { AuthService } from '../../services/auth.service';
import { StockService } from '../../services/stock.service';
import { ExcelExportService } from '../../services/excel-export.service';
import { ScannerCodeBarresComponent } from '../scanner-code-barres/scanner-code-barres';

// Colonnes sur lesquelles le tri est autorisé.
type ColonneTriable = 'nom' | 'prix' | 'quantite';

@Component({
  selector: 'app-produit-list',
  standalone: true,

  imports: [

  CommonModule,
  FormsModule,
  ScannerCodeBarresComponent

],

  templateUrl: './produit-list.html',
  styleUrls: ['./produit-list.css']
})
export class ProduitListComponent implements OnInit {

  // Produits de la page actuellement chargée depuis le backend.
  produits: Produit[] = [];
  // Vue affichée : les produits de la page courante, éventuellement filtrés par la recherche.
  produitsFiltres: Produit[] = [];
  messageSucces: string = '';
  messageErreur: string = '';
  private minuteurMessage?: ReturnType<typeof setTimeout>;

// ===============================
// PAGINATION (pilotée par le backend : Spring Boot renvoie un Page<ProduitDTO>)
// ===============================

// Index de la page courante, base 0 (comme côté Spring Boot).
pageCourante = 0;

// Nombre total de pages renvoyé par le backend (jamais recalculé manuellement).
totalPages = 1;

totalElements = 0;

// Nombre de produits par page : fixé à 5 pour tous les appels HTTP.
private readonly taillePage = 5;

// Texte saisi dans la zone de recherche.
recherche = '';

  // Objet lié au formulaire d'ajout d'un produit.
nouveauProduit: Produit = {

  nom: '',
  description: '',
  prix: 0,
  quantite: 0,
  categorie: '',
  codeBarre: ''

};

// Affiche ou masque la modale de scan caméra.
scannerOuvert = false;

// Contient l'identifiant du produit en cours de modification.
// null signifie que le formulaire est en mode "Ajout".
produitEnModification: number | null = null;

// Identifiants des produits dont la suppression est en cours : permet de désactiver
// le bouton "Supprimer" correspondant pour empêcher un double clic d'envoyer deux
// requêtes DELETE (et donc deux lignes dans le journal d'audit).
idsEnSuppression = new Set<number>();

  // ===============================
  // RÉAPPROVISIONNEMENT (entrée de stock)
  // ===============================

  // Produit ciblé par le panneau de réapprovisionnement (null = panneau fermé).
  produitAReapprovisionner: Produit | null = null;
  quantiteEntree: number = 1;
  motifEntree: string = '';

  constructor(
    private produitService: ProduitService,
    private dataRefreshService: DataRefreshService,
    public authService: AuthService,
    private stockService: StockService,
    private excelExportService: ExcelExportService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.chargerProduits();
  }

  // Charge une page de produits depuis le backend (page=0&size=5 par défaut).
  chargerProduits(page: number = 0): void {

    this.produitService.getProduits(page, this.taillePage).subscribe({

      next: (reponse: any) => {

        // Accepte un Page Spring Boot (cas normal) ou, par sécurité, un tableau brut.
        this.produits = reponse?.content ?? (Array.isArray(reponse) ? reponse : []);
        this.produitsFiltres = [...this.produits];

        // Ne jamais recalculer totalPages à partir de produits.length : ce tableau ne
        // contient que les éléments de la page courante, pas le total. On ne recalcule
        // manuellement que si le backend ne renvoie pas totalPages du tout.
        this.totalPages = reponse?.totalPages !== undefined
          ? (reponse.totalPages > 0 ? reponse.totalPages : 1)
          : (Math.ceil(this.produits.length / this.taillePage) || 1);
        this.pageCourante = reponse?.number ?? page;
        this.totalElements = reponse?.totalElements ?? this.produits.length;
        this.cdr.markForCheck();

      },

      error: (err) => {
        console.error('Erreur lors du chargement des produits :', err);
        this.cdr.markForCheck();
      }

    });

  }

  ajouterProduit(): void {

  // Validation : le prix doit être strictement supérieur à 0.
  if (!this.nouveauProduit.prix || this.nouveauProduit.prix <= 0) {
    this.messageErreur = 'Le prix doit être supérieur à 0.';
    setTimeout(() => { this.messageErreur = ''; this.cdr.markForCheck(); }, 5000);
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

          // Recharge la page courante depuis le backend.
          this.chargerProduits(this.pageCourante);

          // Réinitialise le formulaire.
          this.nouveauProduit = {
            nom: '',
            description: '',
            prix: 0,
            quantite: 0,
            categorie: '',
            codeBarre: ''
          };

          // Retour au mode Ajout.
          this.produitEnModification = null;

          // Message de succès pour modification
          this.messageSucces = 'Produit mis à jour avec succès !';
          this.viderMessageApresDelai();
          this.dataRefreshService.notifyDataChanged();
          this.cdr.markForCheck();

        },

        error: (err) => {
          console.error(err);
          this.messageErreur = err.error?.codeBarre ?? err.error ?? "Impossible d'enregistrer le produit.";
          this.viderMessageApresDelai();
          this.cdr.markForCheck();
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

        // Recharge la page courante depuis le backend.
        this.chargerProduits(this.pageCourante);

        // Vide le formulaire.
        this.nouveauProduit = {
          nom: '',
          description: '',
          prix: 0,
          quantite: 0,
          categorie: '',
          codeBarre: ''
        };

        // Message de succès pour ajout
        this.messageSucces = 'Produit ajouté avec succès !';
        this.viderMessageApresDelai();
        this.dataRefreshService.notifyDataChanged();
        this.cdr.markForCheck();
      },

      error: (err) => {
        console.error(err);
        this.messageErreur = err.error?.codeBarre ?? err.error ?? "Impossible d'ajouter le produit.";
        this.viderMessageApresDelai();
        this.cdr.markForCheck();
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
    quantite: 0,
    categorie: '',
    codeBarre: ''
  };
  // 2. On repasse à null pour revenir au mode "Ajout"
  this.produitEnModification = null;
}

// Supprime un produit de la base de données.
supprimerProduit(id: number): void {
  // 0. Le bouton est déjà désactivé pendant que la requête est en cours, mais on
  // se protège aussi ici contre un second appel programmatique.
  if (this.idsEnSuppression.has(id)) {
    return;
  }

  // 1. Demande de confirmation préalable
  if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {

    this.idsEnSuppression.add(id);

    this.produitService.supprimerProduit(id).subscribe({
      next: () => {
        // 2. Retrait immédiat de la ligne supprimée : l'utilisateur la voit
        // disparaître dès la réponse du backend, sans attendre un second appel.
        this.produits = this.produits.filter(p => p.id !== id);
        this.produitsFiltres = this.produitsFiltres.filter(p => p.id !== id);
        this.idsEnSuppression.delete(id);

        // 3. Message de succès
        this.messageSucces = 'Produit supprimé avec succès !';
        this.viderMessageApresDelai();

        // 4. Recharge la page courante depuis le backend : après une suppression,
        // le nombre total de pages peut changer.
        this.chargerProduits(this.pageCourante);
        this.dataRefreshService.notifyDataChanged();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.idsEnSuppression.delete(id);
        console.error('Erreur lors de la suppression du produit :', err);
        alert('Impossible de supprimer ce produit.');
        this.cdr.markForCheck();
      }
    });
  }
}

private viderMessageApresDelai(): void {
  clearTimeout(this.minuteurMessage);
  this.minuteurMessage = setTimeout(() => {
    this.messageSucces = '';
    this.messageErreur = '';
    this.cdr.markForCheck();
  }, 3000);
}

// Recherche des produits par nom, en temps réel. Ne filtre que la page actuellement
// chargée (5 produits) : une recherche sur l'ensemble du catalogue nécessiterait un
// paramètre de recherche côté backend, hors de ce périmètre.
rechercherProduit(): void {

  const valeur = this.recherche.toLowerCase().trim();

  this.produitsFiltres = this.produits.filter(produit =>

    produit.nom.toLowerCase().includes(valeur)

  );

}

// ===============================
// SCAN CAMÉRA (code-barres / QR code)
// ===============================

ouvrirScanner(): void {
  this.scannerOuvert = true;
}

fermerScanner(): void {
  this.scannerOuvert = false;
}

// Un code a été détecté par la caméra : on recherche le produit correspondant sur
// l'ensemble du catalogue (pas seulement la page affichée) et on l'affiche seul dans
// le tableau, comme le ferait une recherche par nom.
onCodeScanne(code: string): void {

  this.produitService.rechercherParCodeBarre(code).subscribe({

    next: (produit) => {
      this.recherche = produit.nom;
      this.produitsFiltres = [produit];
      this.messageSucces = `Produit trouvé : ${produit.nom}`;
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
    },

    error: (err) => {
      console.error('Erreur lors de la recherche par code-barres :', err);
      this.messageErreur = `Aucun produit ne correspond au code scanné : ${code}`;
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
    }

  });

}

// Passe à la page suivante (backend : pageCourante + 1).
pageSuivante(): void {

  if (this.pageCourante < this.totalPages - 1) {

    this.chargerProduits(this.pageCourante + 1);

  }

}

// Revient à la page précédente (backend : pageCourante - 1).
pagePrecedente(): void {

  if (this.pageCourante > 0) {

    this.chargerProduits(this.pageCourante - 1);

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

// Trie les produits de la page actuellement affichée.
trier(colonne: ColonneTriable): void {

  // Si on clique deux fois sur la même colonne,
  // on inverse le sens du tri.
  if (this.colonneTri === colonne) {

    this.ordreCroissant = !this.ordreCroissant;

  } else {

    this.colonneTri = colonne;
    this.ordreCroissant = true;

  }

  this.produitsFiltres.sort((a, b) => {

    if (a[colonne] < b[colonne]) {
      return this.ordreCroissant ? -1 : 1;
    }

    if (a[colonne] > b[colonne]) {
      return this.ordreCroissant ? 1 : -1;
    }

    return 0;

  });

}

// Ouvre le panneau de réapprovisionnement pour le produit sélectionné.
ouvrirReapprovisionnement(produit: Produit): void {
  this.produitAReapprovisionner = produit;
  this.quantiteEntree = 1;
  this.motifEntree = '';
}

// Ferme le panneau sans enregistrer.
fermerReapprovisionnement(): void {
  this.produitAReapprovisionner = null;
}

// Enregistre l'entrée de stock puis rafraîchit la page courante.
confirmerReapprovisionnement(): void {

  if (!this.produitAReapprovisionner || !this.quantiteEntree || this.quantiteEntree <= 0) {
    this.messageErreur = 'La quantité doit être supérieure à 0.';
    this.viderMessageApresDelai();
    this.cdr.markForCheck();
    return;
  }

  this.stockService.enregistrerEntree({
    produitId: this.produitAReapprovisionner.id!,
    quantite: this.quantiteEntree,
    motif: this.motifEntree
  }).subscribe({

    next: () => {

      this.messageSucces = 'Stock réapprovisionné avec succès !';
      this.viderMessageApresDelai();
      this.fermerReapprovisionnement();

      this.chargerProduits(this.pageCourante);
      this.dataRefreshService.notifyDataChanged();
      this.cdr.markForCheck();

    },

    error: (err) => {
      console.error("Erreur lors de l'entrée de stock :", err);
      this.messageErreur = "Impossible d'enregistrer l'entrée de stock.";
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
    }

  });

}

// Exporte tous les produits (pas seulement la page affichée) vers un fichier Excel.
exporterProduits(): void {

  // On redemande l'ensemble des produits au backend : la vue affichée est paginée
  // à 5 éléments, mais l'export doit couvrir tout le catalogue.
  this.produitService.getProduits(0, 10000).subscribe({

    next: (reponse) => {

      const entetes = ['Identifiant / Réf', 'Nom du produit', 'Catégorie', 'Prix (FCFA)', 'Quantité en Stock'];

      const lignes = reponse.content.map(produit => [
        produit.id ?? '',
        produit.nom,
        produit.categorie || 'Non catégorisé',
        produit.prix,
        produit.quantite
      ]);

      this.excelExportService.exportToExcel(entetes, lignes, 'produits', 'Produits');

    },

    error: (err) => {
      console.error("Erreur lors de l'export des produits :", err);
      alert("Impossible d'exporter les produits.");
    }

  });

}

}
