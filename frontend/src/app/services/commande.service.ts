import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Commande } from '../models/commande';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class CommandeService {

  private apiUrl = 'http://localhost:8080/api/commandes';

  // Le token JWT est attaché automatiquement par jwtInterceptor.
  constructor(private http: HttpClient) {}

  // Taille fixée à 5 par défaut pour l'affichage paginé.
  getCommandes(page: number = 0, size: number = 5): Observable<Page<Commande>> {
    return this.http.get<Page<Commande>>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  addCommande(commande: Commande): Observable<Commande> {
    return this.http.post<Commande>(this.apiUrl, commande);
  }

  deleteCommande(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Annule une commande : passe son statut à ANNULE et recrédite le stock côté backend.
  annulerCommande(id: number): Observable<Commande> {
    return this.http.put<Commande>(`${this.apiUrl}/${id}/annuler`, {});
  }

}