import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface LoginResponse {
  token?: string;
  accessToken?: string;
  jwt?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // URL de base de l'API backend.
  private readonly apiUrl = 'http://localhost:8080';
  // Nom de la clé utilisée pour stocker le token dans le localStorage.
  private readonly tokenKey = 'authToken';
  // Nom de la clé utilisée pour stocker le rôle de l'utilisateur connecté.
  private readonly roleKey = 'authRole';

  constructor(private http: HttpClient) {}

  // Envoie les identifiants au backend et enregistre le token et le rôle si la réponse en contient.
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, { username, password }).pipe(
      tap((response) => {
        const token = response?.token ?? response?.accessToken ?? response?.jwt;

        if (token) {
          this.ecrireStockage(this.tokenKey, token);
        }

        if (response?.role) {
          this.ecrireStockage(this.roleKey, response.role);
        }
      })
    );
  }

  // Supprime le token et le rôle stockés côté navigateur.
  logout(): void {
    this.supprimerStockage(this.tokenKey);
    this.supprimerStockage(this.roleKey);
  }

  // Retourne le token enregistré si un utilisateur est connecté.
  getToken(): string | null {
    return this.lireStockage(this.tokenKey);
  }

  // Retourne le rôle de l'utilisateur connecté (ex: "ROLE_ADMIN").
  getRole(): string | null {
    return this.lireStockage(this.roleKey);
  }

  // Indique si l'utilisateur connecté possède exactement ce rôle.
  hasRole(role: string): boolean {
    return this.getRole() === role;
  }

  // Indique si l'utilisateur connecté possède l'un des rôles fournis.
  hasAnyRole(roles: string[]): boolean {
    const role = this.getRole();
    return !!role && roles.includes(role);
  }

  // Indique si un utilisateur possède déjà un token de session.
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // localStorage n'est pas disponible dans l'environnement des tests unitaires (ni en rendu
  // côté serveur) : ces trois méthodes centralisent la vérification pour que le service reste
  // utilisable (sans session) plutôt que de lever une erreur.
  private lireStockage(cle: string): string | null {
    return typeof localStorage === 'undefined' ? null : localStorage.getItem(cle);
  }

  private ecrireStockage(cle: string, valeur: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(cle, valeur);
    }
  }

  private supprimerStockage(cle: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(cle);
    }
  }
}
