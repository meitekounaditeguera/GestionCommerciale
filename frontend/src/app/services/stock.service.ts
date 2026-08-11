import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { EntreeStock, MouvementStock } from '../models/mouvement-stock';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class StockService {

  private apiUrl = 'http://localhost:8080/api/stock';

  // Le token JWT est attaché automatiquement par jwtInterceptor.
  constructor(private http: HttpClient) {}

  enregistrerEntree(requete: EntreeStock): Observable<MouvementStock> {
    return this.http.post<MouvementStock>(`${this.apiUrl}/entree`, requete);
  }

  // Taille fixée à 5 par défaut pour l'affichage paginé.
  getHistorique(page: number = 0, size: number = 5): Observable<Page<MouvementStock>> {
    return this.http.get<Page<MouvementStock>>(`${this.apiUrl}/historique`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }
}
