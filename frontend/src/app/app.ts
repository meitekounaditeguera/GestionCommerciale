import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');

  // Injecté ici (même si non utilisé directement dans le template) pour garantir que
  // le thème sauvegardé est appliqué à <html> dès le tout début du démarrage de
  // l'application, avant même le rendu de la Navbar.
  constructor(private themeService: ThemeService) {}
}

