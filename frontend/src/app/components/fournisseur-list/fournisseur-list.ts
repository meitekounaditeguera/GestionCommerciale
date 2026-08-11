import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';

import { Fournisseur } from '../../models/fournisseur';
import { FournisseurService } from '../../services/fournisseur.service';
import { DataRefreshService } from '../../services/data-refresh.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-fournisseur-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fournisseur-list.html',
  styleUrls: ['./fournisseur-list.css']
})
export class FournisseurListComponent implements OnInit {

  // Fournisseurs de la page actuellement chargée depuis le backend.
  fournisseurs: Fournisseur[] = [];
  // Vue affichée : les fournisseurs de la page courante, éventuellement filtrés par la recherche.
  fournisseursFiltres: Fournisseur[] = [];
  recherche = '';
  messageSucces = '';
  messageErreur = '';
  private minuteurMessage?: ReturnType<typeof setTimeout>;

  // ===============================
  // PAGINATION (pilotée par le backend : Spring Boot renvoie un Page<FournisseurDTO>)
  // ===============================

  // Index de la page courante, base 0 (comme côté Spring Boot).
  pageCourante = 0;

  // Nombre total de pages renvoyé par le backend (jamais recalculé manuellement).
  totalPages = 1;

  totalElements = 0;

  // Nombre de fournisseurs par page : fixé à 5 pour tous les appels HTTP.
  private readonly taillePage = 5;

  // Tri
  colonneTri: keyof Fournisseur = 'nom';
  ordreCroissant = true;

  // Modal de création / modification
  modalOuvert = false;
  modeEdition = false;
  fournisseurEnEditionId: number | null = null;
  fournisseurForm: Fournisseur = this.formulaireVide();

  // Identifiants des fournisseurs dont la suppression est en cours : permet de désactiver
  // le bouton "Supprimer" correspondant pour empêcher un double clic d'envoyer deux
  // requêtes DELETE (et donc deux lignes dans le journal d'audit).
  idsEnSuppression = new Set<number>();

  constructor(
    private fournisseurService: FournisseurService,
    private dataRefreshService: DataRefreshService,
    public authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.chargerFournisseurs();
  }

  // Charge une page de fournisseurs depuis le backend (page=0&size=5 par défaut).
  chargerFournisseurs(page: number = 0): void {
    this.fournisseurService.getFournisseurs(page, this.taillePage).subscribe({
      next: (reponse) => {

        this.fournisseurs = reponse.content;
        this.fournisseursFiltres = [...reponse.content];

        // Ne jamais recalculer totalPages manuellement : reponse.content ne contient
        // que les éléments de la page courante, pas le total.
        this.totalPages = reponse.totalPages > 0 ? reponse.totalPages : 1;
        this.pageCourante = reponse.number;
        this.totalElements = reponse.totalElements;
        this.cdr.markForCheck();

      },
      error: (err) => {
        console.error('Erreur lors du chargement des fournisseurs :', err);
        this.cdr.markForCheck();
      }
    });
  }

  // ===============================
  // MODAL
  // ===============================

  ouvrirCreation(): void {
    this.modeEdition = false;
    this.fournisseurEnEditionId = null;
    this.fournisseurForm = this.formulaireVide();
    this.modalOuvert = true;
  }

  ouvrirEdition(fournisseur: Fournisseur): void {
    this.modeEdition = true;
    this.fournisseurEnEditionId = fournisseur.id!;
    this.fournisseurForm = { ...fournisseur };
    this.modalOuvert = true;
  }

  fermerModal(): void {
    this.modalOuvert = false;
  }

  enregistrer(form: NgForm): void {

    if (form.invalid) {
      return;
    }

    if (this.modeEdition && this.fournisseurEnEditionId !== null) {

      this.fournisseurService.updateFournisseur(this.fournisseurEnEditionId, this.fournisseurForm).subscribe({
        next: () => {
          this.chargerFournisseurs(this.pageCourante);
          this.fermerModal();
          this.messageSucces = 'Fournisseur mis à jour avec succès !';
          this.viderMessageApresDelai();
          this.dataRefreshService.notifyDataChanged();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Erreur lors de la modification :', err);
          this.messageErreur = typeof err.error === 'string' ? err.error : "Impossible de modifier le fournisseur.";
          this.viderMessageApresDelai();
          this.cdr.markForCheck();
        }
      });

    } else {

      this.fournisseurService.addFournisseur(this.fournisseurForm).subscribe({
        next: () => {
          this.chargerFournisseurs(this.pageCourante);
          this.fermerModal();
          this.messageSucces = 'Fournisseur ajouté avec succès !';
          this.viderMessageApresDelai();
          this.dataRefreshService.notifyDataChanged();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error("Erreur lors de l'ajout :", err);
          this.messageErreur = typeof err.error === 'string' ? err.error : "Impossible d'ajouter le fournisseur.";
          this.viderMessageApresDelai();
          this.cdr.markForCheck();
        }
      });

    }

  }

  supprimerFournisseur(id: number): void {
    // Le bouton est déjà désactivé pendant que la requête est en cours, mais on
    // se protège aussi ici contre un second appel programmatique.
    if (this.idsEnSuppression.has(id)) {
      return;
    }

    if (confirm('Êtes-vous sûr de vouloir supprimer ce fournisseur ?')) {

      this.idsEnSuppression.add(id);

      this.fournisseurService.deleteFournisseur(id).subscribe({
        next: () => {
          // Retrait immédiat de la ligne supprimée : l'utilisateur la voit
          // disparaître dès la réponse du backend, sans attendre un second appel.
          this.fournisseurs = this.fournisseurs.filter(f => f.id !== id);
          this.fournisseursFiltres = this.fournisseursFiltres.filter(f => f.id !== id);
          this.idsEnSuppression.delete(id);

          this.messageSucces = 'Fournisseur supprimé avec succès !';
          this.viderMessageApresDelai();

          // Recharge la page courante depuis le backend : après une suppression,
          // le nombre total de pages peut changer.
          this.chargerFournisseurs(this.pageCourante);
          this.dataRefreshService.notifyDataChanged();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.idsEnSuppression.delete(id);
          console.error('Erreur lors de la suppression :', err);
          alert('Impossible de supprimer ce fournisseur.');
          this.cdr.markForCheck();
        }
      });
    }
  }

  private formulaireVide(): Fournisseur {
    return { nom: '', email: '', telephone: '', adresse: '' };
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
  // RECHERCHE / TRI
  // ===============================

  // Ne filtre que la page actuellement chargée (5 fournisseurs) : une recherche sur
  // l'ensemble des fournisseurs nécessiterait un paramètre de recherche côté backend,
  // hors de ce périmètre.
  filtrerFournisseurs(): void {

    const valeur = this.recherche.toLowerCase().trim();

    this.fournisseursFiltres = this.fournisseurs.filter(f =>
      f.nom.toLowerCase().includes(valeur) ||
      f.email.toLowerCase().includes(valeur)
    );

  }

  trier(colonne: keyof Fournisseur): void {

    if (this.colonneTri === colonne) {
      this.ordreCroissant = !this.ordreCroissant;
    } else {
      this.colonneTri = colonne;
      this.ordreCroissant = true;
    }

    this.fournisseursFiltres.sort((a, b) => {
      const valeurA = String(a[colonne] ?? '').toLowerCase();
      const valeurB = String(b[colonne] ?? '').toLowerCase();
      const comparaison = valeurA.localeCompare(valeurB);
      return this.ordreCroissant ? comparaison : -comparaison;
    });

  }

  // Passe à la page suivante (backend : pageCourante + 1).
  pageSuivante(): void {
    if (this.pageCourante < this.totalPages - 1) {
      this.chargerFournisseurs(this.pageCourante + 1);
    }
  }

  // Revient à la page précédente (backend : pageCourante - 1).
  pagePrecedente(): void {
    if (this.pageCourante > 0) {
      this.chargerFournisseurs(this.pageCourante - 1);
    }
  }

}
