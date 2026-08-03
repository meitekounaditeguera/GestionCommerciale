import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm} from '@angular/forms';

import { Client } from '../../models/client';
import { ClientService } from '../../services/client';


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

// Tableau qui contiendra tous les clients récupérés depuis Spring Boot.
  clients: Client[] = [];

  
// Tableau qui contiendra les clients filtrés selon la recherche.
  clientsFiltres: Client[] = [];
  recherche: string = '';
  messageSucces: string = '';
  messageErreur: string = '';

// Nombre de clients affichés par page.
  taillePage = 5;
// Page actuelle.
  pageActuelle = 1;

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
  telephone: ''
};

// Contient l'identifiant du client en cours de modification.
// null signifie que nous sommes en mode "Ajout".
clientEnModification: number | null = null;

// Injection du ClientService.
// Angular fournit automatiquement une instance du service.
constructor(private clientService: ClientService, private cdr: ChangeDetectorRef) {}

  // ngOnInit() est exécutée automatiquement au chargement du composant.
  ngOnInit(): void {

  this.chargerClients();

}

  chargerClients(): void {

  this.clientService.getClients().subscribe({

    next: (data) => {

      this.clients = data;
      this.clientsFiltres = data;

    },

    error: (err) => {

      console.error(err);

    }

  });

}

// Filtre les clients par nom, prénom ou email, en temps réel.
filtrerClients(): void {

  const valeur = this.recherche.toLowerCase().trim();

  this.clientsFiltres = this.clients.filter(client =>
    client.nom.toLowerCase().includes(valeur) ||
    client.prenom.toLowerCase().includes(valeur) ||
    client.email.toLowerCase().includes(valeur)
  );

  // Retour à la première page pour éviter une pagination hors limites.
  this.pageActuelle = 1;

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
    setTimeout(() => (this.messageErreur = ''), 3000);
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

          // On remplace le client dans le tableau.
          this.chargerClients();

          // On vide le formulaire.
          this.nouveauClient = {
            nom: '',
            prenom: '',
            email: '',
            telephone: ''
          };

          // On revient en mode Ajout.
          this.clientEnModification = null;

          // Message de succès pour modification
          this.messageSucces = 'Client mis à jour avec succès !';
          setTimeout(() => (this.messageSucces = ''), 3000);

        },

        error: (err) => {
          console.error("Erreur de modification :", err);
        }

      });

  }

 
  // ============================
  // MODE AJOUT
  // ============================
  else {

    this.clientService.addClient(this.nouveauClient).subscribe({

      next: (client) => {

        this.chargerClients();

        this.nouveauClient = {
          nom: '',
          prenom: '',
          email: '',
          telephone: ''
        };

        // Message de succès pour ajout
        this.messageSucces = 'Client ajouté avec succès !';
        setTimeout(() => (this.messageSucces = ''), 3000);

      },

      error: (err) => {
        console.error("Erreur lors de l'ajout :", err);
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
    telephone: ''
  };
  // 2. On repasse l'identifiant à null pour revenir au mode "Ajout"
  this.clientEnModification = null;
}

// Supprime un client après confirmation
supprimerClient(id: number): void {
  // 1. Demander confirmation AVANT de supprimer
  if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
    
    this.clientService.deleteClient(id).subscribe({
      next: () => {
        // 2. Mettre à jour la liste en mémoire IMMÉDIATEMENT
        // Le tableau affiché se base sur clientsFiltres : il faut aussi le mettre à jour,
        // sinon la ligne reste visible tant que chargerClients() n'est pas rappelé.
        this.clients = this.clients.filter(c => c.id !== id);
        this.clientsFiltres = this.clientsFiltres.filter(c => c.id !== id);

        // 3. Forcer la détection des changements Angular
        this.cdr.detectChanges();

        // 4. Message de confirmation / succès
        alert('Client supprimé avec succès !');
      },
      error: (err) => {
        console.error('Erreur lors de la suppression :', err);
        alert('Une erreur est survenue lors de la suppression.');
      }
    });

  }
}

// Retourne le nombre total de pages.
get nombrePages(): number {

  return Math.ceil(
    this.clientsFiltres.length / this.taillePage
  );

}

// Retourne uniquement les clients de la page courante.
get clientsPagination(): Client[] {

  const debut = (this.pageActuelle - 1) * this.taillePage;

  const fin = debut + this.taillePage;

  return this.clientsFiltres.slice(debut, fin);

}

// Passe à la page suivante.
pageSuivante(): void {

  if (this.pageActuelle * this.taillePage < this.clientsFiltres.length) {

    this.pageActuelle++;

  }

}

// Revient à la page précédente.
pagePrecedente(): void {

  if (this.pageActuelle > 1) {

    this.pageActuelle--;

  }

}

// Trie les clients selon la colonne sélectionnée.
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

}