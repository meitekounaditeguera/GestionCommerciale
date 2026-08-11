import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CommandeFournisseur } from '../models/commande-fournisseur';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class CommandeFournisseurService {

  private apiUrl = 'http://localhost:8080/api/commandes-fournisseurs';

  // Le token JWT est attaché automatiquement par jwtInterceptor.
  constructor(private http: HttpClient) {}

  // Taille fixée à 5 par défaut pour l'affichage paginé.
  getCommandesFournisseurs(page: number = 0, size: number = 5): Observable<Page<CommandeFournisseur>> {
    return this.http.get<Page<CommandeFournisseur>>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  creerCommandeFournisseur(commande: CommandeFournisseur): Observable<CommandeFournisseur> {
    return this.http.post<CommandeFournisseur>(this.apiUrl, commande);
  }

  validerCommandeFournisseur(id: number): Observable<CommandeFournisseur> {
    return this.http.patch<CommandeFournisseur>(`${this.apiUrl}/${id}/valider`, {});
  }

  annulerCommandeFournisseur(id: number): Observable<CommandeFournisseur> {
    return this.http.patch<CommandeFournisseur>(`${this.apiUrl}/${id}/annuler`, {});
  }

  recevoirCommandeFournisseur(id: number): Observable<CommandeFournisseur> {
    return this.http.post<CommandeFournisseur>(`${this.apiUrl}/${id}/recevoir`, {});
  }

}
