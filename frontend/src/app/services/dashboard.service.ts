import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  DashboardStats,
  CaMensuel,
  VentesParCategorie,
  TopProduit,
  ProduitRupture,
  NouveauxClients,
  ChiffreAffaires,
  MeilleurClient,
  ProduitPhare,
  CategoriePopulaire
} from '../models/dashboard-stats';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = 'http://localhost:8080/api/dashboard';

  // Le token JWT est attaché automatiquement par jwtInterceptor.
  constructor(private http: HttpClient) {}

  getStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/stats`);
  }

  getCaMensuel(): Observable<CaMensuel[]> {
    return this.http.get<CaMensuel[]>(`${this.apiUrl}/stats/ca-mensuel`);
  }

  getVentesParCategorie(): Observable<VentesParCategorie[]> {
    return this.http.get<VentesParCategorie[]>(`${this.apiUrl}/stats/ventes-par-categorie`);
  }

  getTopProduits(): Observable<TopProduit[]> {
    return this.http.get<TopProduit[]>(`${this.apiUrl}/stats/top-produits`);
  }

  getRupturesStock(): Observable<ProduitRupture[]> {
    return this.http.get<ProduitRupture[]>(`${this.apiUrl}/stats/ruptures-stock`);
  }

  getNouveauxClients(): Observable<NouveauxClients> {
    return this.http.get<NouveauxClients>(`${this.apiUrl}/stats/nouveaux-clients`);
  }

  getChiffreAffaires(): Observable<ChiffreAffaires> {
    return this.http.get<ChiffreAffaires>(`${this.apiUrl}/ca`);
  }

  getMeilleurClient(): Observable<MeilleurClient> {
    return this.http.get<MeilleurClient>(`${this.apiUrl}/meilleur-client`);
  }

  getProduitPhare(): Observable<ProduitPhare> {
    return this.http.get<ProduitPhare>(`${this.apiUrl}/produit-phare`);
  }

  getCategoriePopulaire(): Observable<CategoriePopulaire> {
    return this.http.get<CategoriePopulaire>(`${this.apiUrl}/categorie-populaire`);
  }
}
