import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm} from '@angular/forms';

import { Client } from '../../models/client';
import { ClientService } from '../../services/client';
import { DataRefreshService } from '../../services/data-refresh.service';
import { AuthService } from '../../services/auth.service';
import { ExcelExportService } from '../../services/excel-export.service';


// Le décorateur @Component définit les métadonnées du composant.
@Component({
  selector: 'app-client-list',
  standalone: true,

  // CommonModule permet d'utiliser *ngFor, *ngIf...
  imports: [
    CommonModule,
    FormsModule

  ],

  templateUrl: './client-list.html',
  styleUrls: ['./client-list.css']
})

export class ClientListComponent implements OnInit {

// Clients de la page actuellement chargée depuis le backend.
  clients: Client[] = [];

// Vue affichée : les clients de la page courante, éventuellement filtrés par la recherche.
  clientsFiltres: Client[] = [];
  recherche: string = '';
  messageSucces: string = '';
  messageErreur: string = '';
  private minuteurMessage?: ReturnType<typeof setTimeout>;

// ===============================
// PAGINATION (pilotée par le backend : Spring Boot renvoie un Page<ClientDTO>)
// ===============================

// Index de la page courante, base 0 (comme côté Spring Boot).
pageCourante = 0;

// Nombre total de pages renvoyé par le backend (jamais recalculé manuellement).
totalPages = 1;

totalElements = 0;

// Nombre de clients par page : fixé à 5 pour tous les appels HTTP.
private readonly taillePage = 5;

// Colonne actuellement triée.
colonneTri: string = 'id';

// Sens du tri.
// true = croissant
// false = décroissant
ordreCroissant: boolean = true;

// Objet relié au formulaire d'ajout d'un client.
// Chaque champ du formulaire modifiera automatiquement cet objet.
nouveauClient: Client = {
  nom: '',
  prenom: '',
  email: '',
  telephone: '',
  adresse: ''
};

// Contient l'identifiant du client en cours de modification.
// null signifie que nous sommes en mode "Ajout".
clientEnModification: number | null = null;

// Identifiants des clients dont la suppression est en cours : permet de désactiver
// le bouton "Supprimer" correspondant pour empêcher un double clic d'envoyer deux
// requêtes DELETE (et donc deux lignes dans le journal d'audit).
idsEnSuppression = new Set<number>();

// Injection du ClientService.
// Angular fournit automatiquement une instance du service.
constructor(
  private clientService: ClientService,
  private dataRefreshService: DataRefreshService,
  public authService: AuthService,
  private excelExportService: ExcelExportService,
  private cdr: ChangeDetectorRef
) {}

  // ngOnInit() est exécutée automatiquement au chargement du composant.
  ngOnInit(): void {

  this.chargerClients();

}

  // Charge une page de clients depuis le backend (page=0&size=5 par défaut).
  chargerClients(page: number = 0): void {

  this.clientService.getClients(page, this.taillePage).subscribe({

    next: (reponse: any) => {

      // Accepte un Page Spring Boot (cas normal) ou, par sécurité, un tableau brut.
      this.clients = reponse?.content ?? (Array.isArray(reponse) ? reponse : []);
      this.clientsFiltres = [...this.clients];

      // Ne jamais recalculer totalPages à partir de clients.length : ce tableau ne
      // contient que les éléments de la page courante, pas le total. On ne recalcule
      // manuellement que si le backend ne renvoie pas totalPages du tout.
      this.totalPages = reponse?.totalPages !== undefined
        ? (reponse.totalPages > 0 ? reponse.totalPages : 1)
        : (Math.ceil(this.clients.length / this.taillePage) || 1);
      this.pageCourante = reponse?.number ?? page;
      this.totalElements = reponse?.totalElements ?? this.clients.length;
      this.cdr.markForCheck();

    },

    error: (err) => {

      console.error(err);
      this.cdr.markForCheck();

    }

  });

}

// Filtre les clients de la page courante par nom, prénom ou email, en temps réel.
// Ne filtre que la page actuellement chargée (5 clients) : une recherche sur l'ensemble
// des clients nécessiterait un paramètre de recherche côté backend, hors de ce périmètre.
filtrerClients(): void {

  const valeur = this.recherche.toLowerCase().trim();

  this.clientsFiltres = this.clients.filter(client =>
    client.nom.toLowerCase().includes(valeur) ||
    client.prenom.toLowerCase().includes(valeur) ||
    client.email.toLowerCase().includes(valeur)
  );

}

  // Ajoute un nouveau client ou modifie un client existant.
ajouterClient(form: NgForm): void {
  // ⛔ Si le formulaire comporte des erreurs, on ne fait pas l'appel Backend !
  if (form.invalid) {
    return;
  }

  // ⛔ Un email déjà utilisé par un AUTRE client n'est pas autorisé.
  const emailSaisi = this.nouveauClient.email.toLowerCase().trim();
  const emailDejaUtilise = this.clients.some(client =>
    client.email.toLowerCase() === emailSaisi &&
    client.id !== this.clientEnModification
  );

  if (emailDejaUtilise) {
    this.messageErreur = 'Cet email existe déjà, veuillez renseigner un autre email.';
    setTimeout(() => { this.messageErreur = ''; this.cdr.markForCheck(); }, 3000);
    this.cdr.markForCheck();
    return;
  }

  // ============================
  // MODE MODIFICATION
  // ============================
  if (this.clientEnModification !== null) {

    this.clientService
      .updateClient(this.clientEnModification, this.nouveauClient)
      .subscribe({

        next: (clientModifie) => {

          // Recharge la page courante depuis le backend.
          this.chargerClients(this.pageCourante);

          // On vide le formulaire.
          this.nouveauClient = {
            nom: '',
            prenom: '',
            email: '',
            telephone: '',
            adresse: ''
          };

          // On revient en mode Ajout.
          this.clientEnModification = null;

          // Message de succès pour modification
          this.messageSucces = 'Client mis à jour avec succès !';
          this.viderMessageApresDelai();
          this.dataRefreshService.notifyDataChanged();
          this.cdr.markForCheck();

        },

        error: (err) => {
          console.error("Erreur de modification :", err);
          this.messageErreur = typeof err.error === 'string' ? err.error : (err.error?.email ?? "Impossible de modifier le client.");
          this.viderMessageApresDelai();
          this.cdr.markForCheck();
        }

      });

  }


  // ============================
  // MODE AJOUT
  // ============================
  else {

    this.clientService.addClient(this.nouveauClient).subscribe({

      next: (client) => {

        this.chargerClients(this.pageCourante);

        this.nouveauClient = {
          nom: '',
          prenom: '',
          email: '',
          telephone: '',
          adresse: ''
        };

        // Message de succès pour ajout
        this.messageSucces = 'Client ajouté avec succès !';
        this.viderMessageApresDelai();
        this.dataRefreshService.notifyDataChanged();
        this.cdr.markForCheck();

      },

      error: (err) => {
        console.error("Erreur lors de l'ajout :", err);
        this.messageErreur = typeof err.error === 'string' ? err.error : (err.error?.email ?? "Impossible d'ajouter le client.");
        this.viderMessageApresDelai();
        this.cdr.markForCheck();
      }

    });

  }

}

// Charge les informations du client dans le formulaire
// afin de pouvoir les modifier.
modifierClient(client: Client): void {

  // On mémorise son identifiant.
  this.clientEnModification = client.id!;

  // On copie les informations du client.
  this.nouveauClient = { ...client };

}

// Annuler une modification
annulerModification(): void {
  // 1. On vide le formulaire
  this.nouveauClient = {
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    adresse: ''
  };
  // 2. On repasse l'identifiant à null pour revenir au mode "Ajout"
  this.clientEnModification = null;
}

// Supprime un client après confirmation
supprimerClient(id: number): void {
  // 0. Le bouton est déjà désactivé pendant que la requête est en cours, mais on
  // se protège aussi ici contre un second appel programmatique.
  if (this.idsEnSuppression.has(id)) {
    return;
  }

  // 1. Demander confirmation AVANT de supprimer
  if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {

    this.idsEnSuppression.add(id);

    this.clientService.deleteClient(id).subscribe({
      next: () => {

        // 2. Retrait immédiat de la ligne supprimée : l'utilisateur la voit
        // disparaître dès la réponse du backend, sans attendre un second appel.
        this.clients = this.clients.filter(c => c.id !== id);
        this.clientsFiltres = this.clientsFiltres.filter(c => c.id !== id);
        this.idsEnSuppression.delete(id);

        // 3. Message de confirmation / succès
        this.messageSucces = 'Client supprimé avec succès !';
        this.viderMessageApresDelai();

        // 4. Recharge la page courante depuis le backend : après une suppression,
        // le nombre total de pages peut changer.
        this.chargerClients(this.pageCourante);
        this.dataRefreshService.notifyDataChanged();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.idsEnSuppression.delete(id);
        console.error('Erreur lors de la suppression :', err);
        alert('Une erreur est survenue lors de la suppression.');
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

// Passe à la page suivante (backend : pageCourante + 1).
pageSuivante(): void {

  if (this.pageCourante < this.totalPages - 1) {

    this.chargerClients(this.pageCourante + 1);

  }

}

// Revient à la page précédente (backend : pageCourante - 1).
pagePrecedente(): void {

  if (this.pageCourante > 0) {

    this.chargerClients(this.pageCourante - 1);

  }

}

// Trie les clients de la page actuellement affichée.
trier(colonne: keyof Client): void {

  // Si on clique sur la même colonne,
  // on inverse simplement le sens.
  if (this.colonneTri === colonne) {

    this.ordreCroissant = !this.ordreCroissant;

  } else {

    this.colonneTri = colonne;
    this.ordreCroissant = true;

  }

  this.clientsFiltres.sort((a, b) => {

    const valeurA = String(a[colonne]).toLowerCase();
    const valeurB = String(b[colonne]).toLowerCase();

    const comparaison = valeurA.localeCompare(valeurB);

    return this.ordreCroissant ? comparaison : -comparaison;

  });

}

// Exporte tous les clients (pas seulement la page affichée) vers un fichier Excel.
exporterClients(): void {

  // On redemande l'ensemble des clients au backend : la vue affichée est paginée
  // à 5 éléments, mais l'export doit couvrir toutes les données.
  this.clientService.getClients(0, 10000).subscribe({

    next: (reponse) => {

      const entetes = ['Nom & Prénom', 'Email', 'Téléphone', 'Adresse'];

      const lignes = reponse.content.map(client => [
        `${client.nom} ${client.prenom}`,
        client.email,
        client.telephone,
        client.adresse || ''
      ]);

      this.excelExportService.exportToExcel(entetes, lignes, 'clients', 'Clients');

    },

    error: (err) => {
      console.error("Erreur lors de l'export des clients :", err);
      alert("Impossible d'exporter les clients.");
    }

  });

}

}
