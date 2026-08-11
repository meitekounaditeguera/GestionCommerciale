import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../models/client';
import { Page } from '../models/page';


@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private apiUrl = 'http://localhost:8080/api/clients';

// Injection du service HttpClient pour effectuer des requêtes HTTP
// Le token JWT est attaché automatiquement par jwtInterceptor.
  constructor(private http: HttpClient) {}

  // Récupère une page de clients depuis le backend (Spring Boot). Taille fixée à 5 par défaut
  // pour l'affichage paginé ; les sélecteurs qui ont besoin de tous les clients passent une
  // taille explicitement plus grande (ex: getClients(0, 1000)).
  getClients(page: number = 0, size: number = 5): Observable<Page<Client>> {
    return this.http.get<Page<Client>>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  // Envoie un nouveau client au backend (Spring Boot)
  addClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }

  // Met à jour un client existant
  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/${id}`, client);
  }

  // Supprime un client de la base de données
  deleteClient(id: number): Observable<void> {
    // On envoie une requête DELETE à l'URL : http://localhost:8080/api/clients/{id}
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}