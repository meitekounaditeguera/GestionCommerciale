import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  // Indique si la requête de connexion est en cours.
  isLoading = false;
  // Indique si le mot de passe est affiché en clair.
  showPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  // Gère la soumission du formulaire de connexion.
  onSubmit(): void {
    if (!this.username.trim() || !this.password.trim()) {
      this.errorMessage = 'Veuillez saisir votre identifiant et votre mot de passe.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // Appelle le service d’authentification et redirige vers la page d’accueil en cas de succès.
    this.authService.login(this.username.trim(), this.password).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/']);
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Échec de la connexion. Vérifiez vos identifiants.';
        this.cdr.markForCheck();
      }
    });
  }

  // Bascule l'affichage du mot de passe entre masqué et en clair.
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
