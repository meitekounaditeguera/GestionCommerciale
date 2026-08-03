import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../models/client';


@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private apiUrl = 'http://localhost:8080/api/clients';

// Injection du service HttpClient pour effectuer des requêtes HTTP
  constructor(private http: HttpClient) {}

  // Récupère la liste des clients depuis le backend (Spring Boot)
  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  // Envoie un nouveau client au backend (Spring Boot)
  addClient(client: Client): Observable<Client> {
    return this.http.post<Client>(
      this.apiUrl, 
      client);
  }

  // Met à jour un client existant
  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(
      `${this.apiUrl}/${id}`,
       client);
}

  // Supprime un client de la base de données
  deleteClient(id: number): Observable<void> {
    // On envoie une requête DELETE à l'URL : http://localhost:8080/api/clients/{id}
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`);
  }

  

}