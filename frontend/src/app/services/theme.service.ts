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
    localStorage.setItem(CLE_STOCKAGE, theme);
  }

  private appliquerTheme(theme: Theme): void {
    document.documentElement.setAttribute('data-bs-theme', theme);
  }

  private lireThemeInitial(): Theme {
    return localStorage.getItem(CLE_STOCKAGE) === 'dark' ? 'dark' : 'light';
  }
}
