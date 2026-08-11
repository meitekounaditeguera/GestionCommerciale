import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CommandeService } from '../../services/commande.service';
import { Commande } from '../../models/commande';
import { Client } from '../../models/client';
import { ClientService } from '../../services/client';
import { Produit } from '../../models/produit';
import { ProduitService } from '../../services/produit.service';
import { LigneCommande } from '../../models/lignecommande';
import { DataRefreshService } from '../../services/data-refresh.service';
import { ExcelExportService } from '../../services/excel-export.service';
import { FacturePdfService } from '../../services/facture-pdf.service';
import { ScannerCodeBarresComponent } from '../scanner-code-barres/scanner-code-barres';

@Component({
  selector: 'app-commande-list',

  imports: [
  CommonModule,
  FormsModule,
  ScannerCodeBarresComponent
],

  templateUrl: './commande-list.html',
  styleUrl: './commande-list.css',
})

export class CommandeListComponent implements OnInit {

clients: Client[] = [];
commandes: Commande[] = [];
produits: Produit[] = [];
messageSucces = '';

// ===============================
// PAGINATION (pilotée par le backend : Spring Boot renvoie un Page<CommandeDTO>)
// ===============================

// Index de la page courante, base 0 (comme côté Spring Boot).
pageCourante = 0;

// Nombre total de pages renvoyé par le backend (jamais recalculé manuellement).
totalPages = 1;

totalElements = 0;

// Nombre de commandes par page : fixé à 5 pour tous les appels HTTP.
private readonly tailleDePage = 5;

// Nombre d'éléments demandé pour les sélecteurs (client/produit) qui ont besoin de
// l'ensemble du catalogue, et non d'une page de 5 éléments.
private readonly tailleSelecteur = 1000;

nouvelleCommande: Commande = {
  dateCommande: '',
  clientId: 0,
  lignes: []
};

nouvelleLigne: LigneCommande = {
  produitId: 0,
  quantite: 1
};

// Affiche ou masque la modale de scan caméra.
scannerOuvert = false;
messageErreur = '';
private minuteurMessage?: ReturnType<typeof setTimeout>;

constructor(
  private commandeService: CommandeService,
  private clientService: ClientService,
  private produitService: ProduitService,
  private dataRefreshService: DataRefreshService,
  private excelExportService: ExcelExportService,
  private facturePdfService: FacturePdfService,
  private cdr: ChangeDetectorRef
) {}

ngOnInit(): void {

  this.chargerCommandes();
  this.chargerClients();
  this.chargerProduits();

}

// Charge une page de commandes depuis le backend (page=0&size=5 par défaut).
chargerCommandes(page: number = 0): void {

  this.commandeService.getCommandes(page, this.tailleDePage).subscribe({

    next: (reponse: any) => {

      // Accepte un Page Spring Boot (cas normal) ou, par sécurité, un tableau brut.
      this.commandes = reponse?.content ?? (Array.isArray(reponse) ? reponse : []);

      // Ne jamais recalculer totalPages à partir de commandes.length : ce tableau ne
      // contient que les éléments de la page courante, pas le total. On ne recalcule
      // manuellement que si le backend ne renvoie pas totalPages du tout.
      this.totalPages = reponse?.totalPages !== undefined
        ? (reponse.totalPages > 0 ? reponse.totalPages : 1)
        : (Math.ceil(this.commandes.length / this.tailleDePage) || 1);
      this.pageCourante = reponse?.number ?? page;
      this.totalElements = reponse?.totalElements ?? this.commandes.length;
      this.cdr.markForCheck();

    },

    error: (err) => {
      console.error(err);
      this.cdr.markForCheck();
    }

  });

}

ajouterCommande(): void {


  this.commandeService.addCommande(this.nouvelleCommande).subscribe({

    next: (commande) => {

  this.messageSucces = '✅ Commande enregistrée avec succès !';
  this.viderMessageApresDelai();

  this.nouvelleCommande = {
    dateCommande: '',
    clientId: 0,
    lignes: []
  };

  this.chargerCommandes(this.pageCourante);
  this.dataRefreshService.notifyDataChanged();
  this.cdr.markForCheck();

},

    error: (err) => {
      console.error(err);
      this.messageErreur = typeof err.error === 'string' ? err.error : "Impossible d'enregistrer la commande.";
      this.viderMessageApresDelai();
      this.cdr.markForCheck();
    }

  });

}

// Charge la liste complète des clients pour peupler le sélecteur du formulaire :
// on demande volontairement une grande taille de page (pas les 5 éléments de la pagination).
chargerClients(): void {

  this.clientService.getClients(0, this.tailleSelecteur).subscribe({

    next: (reponse) => {
      this.clients = reponse.content;
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

private viderMessageApresDelai(): void {
  clearTimeout(this.minuteurMessage);
  this.minuteurMessage = setTimeout(() => {
    this.messageSucces = '';
    this.messageErreur = '';
    this.cdr.markForCheck();
  }, 3000);
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

// Un code a été détecté par la caméra : on cherche le produit correspondant et,
// si trouvé, on le sélectionne directement dans le formulaire d'ajout de ligne.
onCodeScanne(code: string): void {

  this.produitService.rechercherParCodeBarre(code).subscribe({

    next: (produit) => {
      this.nouvelleLigne.produitId = produit.id!;
      this.messageSucces = `Produit sélectionné : ${produit.nom}`;
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

// Génère la facture PDF d'une commande directement dans le navigateur (jsPDF + autoTable,
// format A4) et déclenche son téléchargement. Les données nécessaires (commande, client,
// catalogue produits) sont déjà chargées en mémoire : aucun appel réseau supplémentaire.
genererFacturePDF(commandeId: number): void {

  const commande = this.commandes.find(c => c.id === commandeId);

  if (!commande) {
    alert('Commande introuvable.');
    return;
  }

  try {
    const client = this.clients.find(c => c.id === commande.clientId);
    this.facturePdfService.genererFacturePDF(commande, client, this.produits);
  } catch (err) {
    console.error('Erreur lors de la génération de la facture PDF :', err);
    alert('Impossible de générer la facture PDF.');
  }

}

// Annule une commande VALIDE : le backend recrédite le stock et bascule le statut à ANNULE.
annulerCommande(commande: Commande): void {

  if (!confirm('Voulez-vous vraiment annuler cette commande ? Le stock sera recrédité.')) {
    return;
  }

  this.commandeService.annulerCommande(commande.id!).subscribe({

    next: (commandeAnnulee) => {

      // Mise à jour instantanée du statut affiché, sans recharger la page.
      commande.statut = commandeAnnulee?.statut ?? 'ANNULE';

      this.messageSucces = 'Commande annulée et stock mis à jour';
      this.viderMessageApresDelai();

      // Le stock a changé : on rafraîchit la liste de produits utilisée par le formulaire
      // et on notifie les autres composants (Dashboard, Navbar, liste des produits).
      this.chargerProduits();
      this.dataRefreshService.notifyDataChanged();
      this.cdr.markForCheck();

    },

    error: (err) => {
      console.error("Erreur lors de l'annulation de la commande :", err);
      alert("Impossible d'annuler cette commande.");
      this.cdr.markForCheck();
    }

  });

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

// Passe à la page suivante (backend : pageCourante + 1).
pageSuivante(): void {
  if (this.pageCourante < this.totalPages - 1) {
    this.chargerCommandes(this.pageCourante + 1);
  }
}

// Revient à la page précédente (backend : pageCourante - 1).
pagePrecedente(): void {
  if (this.pageCourante > 0) {
    this.chargerCommandes(this.pageCourante - 1);
  }
}

// Exporte toutes les ventes (pas seulement la page affichée) vers un fichier Excel.
exporterVentes(): void {

  // On redemande l'ensemble des commandes et des clients au backend : la vue affichée
  // est paginée à 5 éléments, mais l'export doit couvrir toutes les données.
  this.commandeService.getCommandes(0, 10000).subscribe({

    next: (reponseCommandes) => {

      this.clientService.getClients(0, 10000).subscribe({

        next: (reponseClients) => {

          const nomsClients = new Map<number, string>();
          reponseClients.content.forEach(client => {
            if (client.id !== undefined) {
              nomsClients.set(client.id, `${client.nom} ${client.prenom}`);
            }
          });

          const entetes = ['Référence Commande', 'Date', 'Client', 'Montant Total (FCFA)', 'Statut'];

          const lignes = reponseCommandes.content.map(commande => [
            this.genererReferenceCommande(commande),
            commande.dateCommande,
            nomsClients.get(commande.clientId) ?? 'Client inconnu',
            commande.montantTotal ?? 0,
            'Validée'
          ]);

          this.excelExportService.exportToExcel(entetes, lignes, 'ventes', 'Ventes');

        },

        error: (err) => {
          console.error(err);
          alert("Impossible d'exporter les ventes.");
        }

      });

    },

    error: (err) => {
      console.error(err);
      alert("Impossible d'exporter les ventes.");
    }

  });

}

// Reproduit le même format de référence que celui affiché sur la facture PDF
// (FAC-{année}-{id sur 4 chiffres}), sans dépendre d'un champ "référence" persisté :
// la commande de vente n'en a pas, contrairement à la commande fournisseur.
private genererReferenceCommande(commande: Commande): string {
  const annee = commande.dateCommande ? commande.dateCommande.split('-')[0] : new Date().getFullYear().toString();
  const idFormatte = String(commande.id ?? 0).padStart(4, '0');
  return `FAC-${annee}-${idFormatte}`;
}

}
