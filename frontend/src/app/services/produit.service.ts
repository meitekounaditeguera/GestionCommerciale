import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Produit } from '../models/produit';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class ProduitService {
  // Supprime un produit via l'API
  supprimerProduit(id: number): Observable<void> {
    return this.deleteProduit(id);
  }

  // URL de l'API Spring Boot
  private apiUrl = 'http://localhost:8080/api/produits';

  // Le token JWT est attaché automatiquement par jwtInterceptor.
  constructor(private http: HttpClient) {}

  // Retourne une page de produits. Taille fixée à 5 par défaut pour l'affichage paginé ;
  // les sélecteurs qui ont besoin de tout le catalogue passent une taille explicitement
  // plus grande (ex: getProduits(0, 1000)).
  getProduits(page: number = 0, size: number = 5): Observable<Page<Produit>> {
    return this.http.get<Page<Produit>>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  // Ajoute un produit
  addProduit(produit: Produit): Observable<Produit> {
    return this.http.post<Produit>(this.apiUrl, produit);
  }

  // Modifie un produit
  updateProduit(id: number, produit: Produit): Observable<Produit> {
    return this.http.put<Produit>(`${this.apiUrl}/${id}`, produit);
  }

  // Supprime un produit
  deleteProduit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Recherche un produit par son code-barres/QR code (utilisé par le scan caméra).
  rechercherParCodeBarre(code: string): Observable<Produit> {
    return this.http.get<Produit>(`${this.apiUrl}/recherche/code-barre`, {
      params: { code }
    });
  }

}