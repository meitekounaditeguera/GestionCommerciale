import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Fournisseur } from '../models/fournisseur';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class FournisseurService {

  private apiUrl = 'http://localhost:8080/api/fournisseurs';

  // Le token JWT est attaché automatiquement par jwtInterceptor.
  constructor(private http: HttpClient) {}

  // Taille fixée à 5 par défaut pour l'affichage paginé ; les sélecteurs qui ont besoin de
  // tous les fournisseurs passent une taille explicitement plus grande (ex: getFournisseurs(0, 1000)).
  getFournisseurs(page: number = 0, size: number = 5): Observable<Page<Fournisseur>> {
    return this.http.get<Page<Fournisseur>>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  addFournisseur(fournisseur: Fournisseur): Observable<Fournisseur> {
    return this.http.post<Fournisseur>(this.apiUrl, fournisseur);
  }

  updateFournisseur(id: number, fournisseur: Fournisseur): Observable<Fournisseur> {
    return this.http.put<Fournisseur>(`${this.apiUrl}/${id}`, fournisseur);
  }

  deleteFournisseur(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}
