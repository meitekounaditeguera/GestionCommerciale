import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type Theme = 'light' | 'dark';

const CLE_STOCKAGE = 'gc-theme';

// Service partagé (singleton) : suit le thème courant, le persiste dans localStorage
// pour qu'il survive à un F5, et l'applique via l'attribut data-bs-theme sur <html>
// (mécanisme de thème natif de Bootstrap 5.3+, repris automatiquement par tous les
// composants Bootstrap : cartes, tableaux, modales, badges...).
@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  private readonly themeSubject = new BehaviorSubject<Theme>(this.lireThemeInitial());
  readonly theme$ = this.themeSubject.asObservable();

  constructor() {
    // Applique immédiatement le thème sauvegardé (ou 'light' par défaut) dès la
    // création du service, qui a lieu au tout début du démarrage de l'application.
    this.appliquerTheme(this.themeSubject.value);
  }

  get themeActuel(): Theme {
    return this.themeSubject.value;
  }

  basculerTheme(): void {
    this.definirTheme(this.themeActuel === 'dark' ? 'light' : 'dark');
  }

  private definirTheme(theme: Theme): void {
    this.themeSubject.next(theme);
    this.appliquerTheme(theme);
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(CLE_STOCKAGE, theme);
    }
  }

  private appliquerTheme(theme: Theme): void {
    document.documentElement.setAttribute('data-bs-theme', theme);
  }

  // typeof localStorage !== 'undefined' : localStorage n'est pas disponible dans
  // l'environnement des tests unitaires (ni en rendu côté serveur) ; on retombe alors
  // simplement sur le thème par défaut plutôt que de faire planter le service.
  private lireThemeInitial(): Theme {
    if (typeof localStorage === 'undefined') {
      return 'light';
    }
    return localStorage.getItem(CLE_STOCKAGE) === 'dark' ? 'dark' : 'light';
  }
}
