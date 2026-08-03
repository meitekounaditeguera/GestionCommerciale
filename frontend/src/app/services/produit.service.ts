import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Produit } from '../models/produit';

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

  constructor(private http: HttpClient) {}

  // Retourne tous les produits
  getProduits(): Observable<Produit[]> {
    return this.http.get<Produit[]>
    (this.apiUrl);
  }

  // Ajoute un produit
  addProduit(produit: Produit): Observable<Produit> {
    return this.http.post<Produit>
    (this.apiUrl,
         produit);
  }

  // Modifie un produit
  updateProduit(id: number, produit: Produit): Observable<Produit> {
    return this.http.put<Produit>(
      `${this.apiUrl}/${id}`,
      produit
    );
  }

  // Supprime un produit
  deleteProduit(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

}