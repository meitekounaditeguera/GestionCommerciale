import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CommandeFournisseur, LigneCommandeFournisseur, StatutCommandeFournisseur } from '../../models/commande-fournisseur';
import { CommandeFournisseurService } from '../../services/commande-fournisseur.service';
import { Fournisseur } from '../../models/fournisseur';
import { FournisseurService } from '../../services/fournisseur.service';
import { Produit } from '../../models/produit';
import { ProduitService } from '../../services/produit.service';
import { DataRefreshService } from '../../services/data-refresh.service';

@Component({
  selector: 'app-commande-fournisseur',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './commande-fournisseur.html',
  styleUrls: ['./commande-fournisseur.css']
})
export class CommandeFournisseurComponent implements OnInit {

  fournisseurs: Fournisseur[] = [];
  produits: Produit[] = [];
  commandesFournisseurs: CommandeFournisseur[] = [];
  messageSucces = '';
  messageErreur = '';
  private minuteurMessage?: ReturnType<typeof setTimeout>;

  // Filtre par statut ('' = tous les statuts). Ne s'applique qu'à la page actuellement
  // chargée (5 commandes) : un filtre sur l'ensemble des commandes nécessiterait un
  // paramètre côté backend, hors de ce périmètre.
  filtreStatut: StatutCommandeFournisseur | '' = '';

  // ===============================
  // PAGINATION (pilotée par le backend : Spring Boot renvoie un Page<CommandeFournisseurDTO>)
  // ===============================

  // Index de la page courante, base 0 (comme côté Spring Boot).
  pageCourante = 0;

  // Nombre total de pages renvoyé par le backend (jamais recalculé manuellement).
  totalPages = 1;

  totalElements = 0;

  // Nombre de commandes par page : fixé à 5 pour tous les appels HTTP.
  private readonly tailleDePage = 5;

  // Nombre d'éléments demandé pour les sélecteurs (produit/fournisseur) qui ont besoin
  // de l'ensemble du catalogue, et non d'une page de 5 éléments.
  private readonly tailleSelecteur = 1000;

  nouvelleCommande: CommandeFournisseur = {
    dateCommande: '',
    fournisseurId: 0,
    lignes: []
  };

  // Quantité par défaut à 1 (et non 0) : un champ numérique vide/à 0 au premier affichage
  // donne l'impression que le formulaire est cassé et empêche silencieusement l'ajout.
  nouvelleLigne: LigneCommandeFournisseur = {
    produitId: 0,
    quantite: 1,
    prixAchatUnitaire: 0
  };

  // Montant total de la commande en cours de création, recalculé à chaque changement des lignes.
  montantTotal = 0;

  constructor(
    private commandeFournisseurService: CommandeFournisseurService,
    private fournisseurService: FournisseurService,
    private produitService: ProduitService,
    private dataRefreshService: DataRefreshService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.chargerCommandesFournisseurs();
    this.chargerFournisseurs();
    this.chargerProduits();
  }

  // Charge une page de commandes fournisseurs depuis le backend (page=0&size=5 par défaut).
  chargerCommandesFournisseurs(page: number = 0): void {
    this.commandeFournisseurService.getCommandesFournisseurs(page, this.tailleDePage).subscribe({
      next: (reponse) => {

        this.commandesFournisseurs = reponse.content;

        // Ne jamais recalculer totalPages manuellement : reponse.content ne contient
        // que les éléments de la page courante, pas le total.
        this.totalPages = reponse.totalPages > 0 ? reponse.totalPages : 1;
        this.pageCourante = reponse.number;
        this.totalElements = reponse.totalElements;
        this.cdr.markForCheck();

      },
      error: (err) => {
        console.error('Erreur lors du chargement des commandes fournisseurs :', err);
        this.cdr.markForCheck();
      }
    });
  }

  // Charge la liste complète des fournisseurs pour peupler le sélecteur du formulaire :
  // on demande volontairement une grande taille de page (pas les 5 éléments de la pagination).
  chargerFournisseurs(): void {
    this.fournisseurService.getFournisseurs(0, this.tailleSelecteur).subscribe({
      next: (reponse) => {
        this.fournisseurs = reponse.content;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.cdr.markForCheck();
      }
    });
  }

  // Charge la liste complète des produits pour peupler le sélecteur du formulaire :
  // on demande volontairement une grande taille de page (pas les 5 éléments de la pagination).
  chargerProduits(): void {
    this.produitService.getProduits(0, this.tailleSelecteur).subscribe({
      next: (reponse) => {
        this.produits = reponse.content;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.cdr.markForCheck();
      }
    });
  }

  ajouterLigne(): void {

    // Un retour visible plutôt qu'un échec silencieux : sans ça, cliquer sur "Ajouter"
    // sans avoir rempli le sous-formulaire ne fait rien et donne l'impression que le
    // bouton (ou le calcul du total) est cassé.
    if (!this.nouvelleLigne.produitId) {
      this.messageErreur = 'Sélectionnez un produit avant de l\'ajouter à la commande.';
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
      return;
    }

    if (!this.nouvelleLigne.quantite || this.nouvelleLigne.quantite <= 0) {
      this.messageErreur = 'La quantité doit être supérieure à 0.';
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
      return;
    }

    if (this.nouvelleLigne.prixAchatUnitaire < 0) {
      this.messageErreur = 'Le prix d\'achat unitaire ne peut pas être négatif.';
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
      return;
    }

    this.nouvelleCommande.lignes.push({
      produitId: this.nouvelleLigne.produitId,
      quantite: this.nouvelleLigne.quantite,
      prixAchatUnitaire: this.nouvelleLigne.prixAchatUnitaire
    });

    // Réinitialisation du sous-formulaire de saisie pour la PROCHAINE ligne : ces champs
    // ne représentent jamais l'état de la commande elle-même (jamais lus par
    // creerCommandeFournisseur), donc leur valeur ne peut pas bloquer l'enregistrement.
    // La quantité repart à 1 (pas 0) pour que le prochain ajout reste immédiat.
    this.nouvelleLigne = { produitId: 0, quantite: 1, prixAchatUnitaire: 0 };

    // Recalcul automatique du montant total à chaque changement du tableau des lignes.
    this.calculerMontantTotal();
  }

  retirerLigne(index: number): void {
    if (confirm('Voulez-vous vraiment retirer ce produit de la commande ?')) {
      this.nouvelleCommande.lignes.splice(index, 1);
      this.calculerMontantTotal();
    }
  }

  // La validation ne porte que sur la commande elle-même (fournisseur, date, au moins une
  // ligne déjà ajoutée) — l'état du sous-formulaire "nouvelleLigne" (produit/quantité/prix
  // en cours de saisie pour la PROCHAINE ligne) n'est jamais pris en compte ici.
  creerCommandeFournisseur(): void {

    if (!this.nouvelleCommande.fournisseurId || !this.nouvelleCommande.dateCommande || this.nouvelleCommande.lignes.length === 0) {
      this.messageErreur = 'Sélectionnez un fournisseur, une date et au moins un produit.';
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
      return;
    }

    this.commandeFournisseurService.creerCommandeFournisseur(this.nouvelleCommande).subscribe({
      next: () => {

        this.messageSucces = '✅ Commande fournisseur enregistrée avec succès !';
        this.viderMessageApresDelai();

        this.nouvelleCommande = { dateCommande: '', fournisseurId: 0, lignes: [] };
        this.calculerMontantTotal();

        this.chargerCommandesFournisseurs(this.pageCourante);
        this.dataRefreshService.notifyDataChanged();
        this.cdr.markForCheck();

      },
      error: (err) => {
        console.error(err);
        this.messageErreur = typeof err.error === 'string' ? err.error : "Impossible d'enregistrer la commande fournisseur.";
        this.viderMessageApresDelai();
        this.cdr.markForCheck();
      }
    });

  }

  valider(commande: CommandeFournisseur): void {
    this.commandeFournisseurService.validerCommandeFournisseur(commande.id!).subscribe({
      next: () => {
        this.messageSucces = 'Commande validée.';
        this.viderMessageApresDelai();
        this.chargerCommandesFournisseurs(this.pageCourante);
        this.dataRefreshService.notifyDataChanged();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        alert(err.error ?? 'Impossible de valider cette commande.');
        this.cdr.markForCheck();
      }
    });
  }

  annuler(commande: CommandeFournisseur): void {
    if (confirm('Voulez-vous vraiment annuler cette commande fournisseur ?')) {
      this.commandeFournisseurService.annulerCommandeFournisseur(commande.id!).subscribe({
        next: () => {
          this.messageSucces = 'Commande annulée.';
          this.viderMessageApresDelai();
          this.chargerCommandesFournisseurs(this.pageCourante);
          this.dataRefreshService.notifyDataChanged();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error(err);
          alert(err.error ?? "Impossible d'annuler cette commande.");
          this.cdr.markForCheck();
        }
      });
    }
  }

  recevoir(commande: CommandeFournisseur): void {
    if (confirm('Confirmer la réception de cette commande ? Le stock des produits sera mis à jour.')) {
      this.commandeFournisseurService.recevoirCommandeFournisseur(commande.id!).subscribe({
        next: () => {
          this.messageSucces = '📦 Réception validée : le stock a été mis à jour.';
          this.viderMessageApresDelai();
          this.chargerCommandesFournisseurs(this.pageCourante);
          this.dataRefreshService.notifyDataChanged();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error(err);
          alert(err.error ?? 'Impossible de valider la réception de cette commande.');
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

  // Affiche le nom du fournisseur au lieu de son ID.
  getNomFournisseur(fournisseurId: number): string {
    const fournisseur = this.fournisseurs.find(f => f.id === fournisseurId);
    return fournisseur ? fournisseur.nom : 'Fournisseur inconnu';
  }

  // Affiche le nom du produit au lieu de son ID.
  getNomProduit(produitId: number): string {
    const produit = this.produits.find(p => p.id === produitId);
    return produit ? produit.nom : 'Produit inconnu';
  }

  // Recalcule le montant total de la commande en cours de création à partir de ses lignes,
  // et met à jour la propriété affichée par le template.
  calculerMontantTotal(): void {
    this.montantTotal = this.nouvelleCommande.lignes.reduce(
      (total, ligne) => total + (ligne.prixAchatUnitaire * ligne.quantite), 0
    );
  }

  get commandesFiltrees(): CommandeFournisseur[] {
    if (!this.filtreStatut) {
      return this.commandesFournisseurs;
    }
    return this.commandesFournisseurs.filter(c => c.statut === this.filtreStatut);
  }

  // Passe à la page suivante (backend : pageCourante + 1).
  pageSuivante(): void {
    if (this.pageCourante < this.totalPages - 1) {
      this.chargerCommandesFournisseurs(this.pageCourante + 1);
    }
  }

  // Revient à la page précédente (backend : pageCourante - 1).
  pagePrecedente(): void {
    if (this.pageCourante > 0) {
      this.chargerCommandesFournisseurs(this.pageCourante - 1);
    }
  }

}
