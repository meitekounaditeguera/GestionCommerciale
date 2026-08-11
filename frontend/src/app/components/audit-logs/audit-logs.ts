import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuditLog } from '../../models/audit-log';
import { AuditLogService } from '../../services/audit-log.service';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-logs.html',
  styleUrl: './audit-logs.css'
})
export class AuditLogsComponent implements OnInit {

  // Journal d'audit de la page courante, déjà trié du plus récent au plus ancien par le backend.
  logs: AuditLog[] = [];

  // ===============================
  // PAGINATION (pilotée par le backend : Spring Boot renvoie un Page<AuditLogDTO>)
  // ===============================
  pageCourante = 0;
  totalPages = 1;
  totalElements = 0;
  private readonly taillePage = 10;

  // Message affiché à la place du tableau quand le chargement échoue, pour ne pas
  // laisser croire à l'utilisateur qu'il n'y a simplement aucune action enregistrée
  // (ex : 403 si son rôle n'est pas ADMIN, l'endpoint étant réservé aux administrateurs).
  messageErreur = '';

  constructor(private auditLogService: AuditLogService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.chargerLogs();
  }

  // Charge une page du journal d'audit depuis le backend (page=0&size=10 par défaut).
  chargerLogs(page: number = 0): void {

    this.messageErreur = '';

    this.auditLogService.getLogs(page, this.taillePage).subscribe({

      next: (reponse) => {
        this.logs = reponse.content;
        this.totalPages = reponse.totalPages > 0 ? reponse.totalPages : 1;
        this.pageCourante = reponse.number;
        this.totalElements = reponse.totalElements;
        this.cdr.markForCheck();
      },

      error: (err) => {
        console.error("Erreur lors du chargement du journal d'audit :", err);
        this.logs = [];
        this.messageErreur = err.status === 403
          ? "Accès refusé : le journal d'audit est réservé aux administrateurs."
          : "Impossible de charger le journal d'audit. Veuillez réessayer.";
        this.cdr.markForCheck();
      }

    });

  }

  // Libellé français affiché dans le badge, à partir du code d'action renvoyé par le backend.
  libelleAction(action: string): string {
    switch (action) {
      case 'CREATION': return 'Création';
      case 'MODIFICATION': return 'Modification';
      case 'SUPPRESSION': return 'Suppression';
      default: return action;
    }
  }

  // Vert pour une création, orange pour une modification, rouge pour une suppression.
  classeBadgeAction(action: string): string {
    switch (action) {
      case 'CREATION': return 'bg-success';
      case 'MODIFICATION': return 'bg-warning text-dark';
      case 'SUPPRESSION': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  pageSuivante(): void {
    if (this.pageCourante < this.totalPages - 1) {
      this.chargerLogs(this.pageCourante + 1);
    }
  }

  pagePrecedente(): void {
    if (this.pageCourante > 0) {
      this.chargerLogs(this.pageCourante - 1);
    }
  }

}
