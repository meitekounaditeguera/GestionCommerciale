import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuditLog } from '../models/audit-log';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class AuditLogService {

  private apiUrl = 'http://localhost:8080/api/audit-logs';

  // Le token JWT est attaché automatiquement par jwtInterceptor.
  // Réservé aux administrateurs côté backend (SecurityConfig : /api/audit-logs/** -> ROLE_ADMIN).
  constructor(private http: HttpClient) {}

  // Taille fixée à 10 par défaut pour l'affichage paginé, trié du plus récent au plus
  // ancien par le backend (Sort.by("dateAction").descending()).
  getLogs(page: number = 0, size: number = 10): Observable<Page<AuditLog>> {
    return this.http.get<Page<AuditLog>>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

}
