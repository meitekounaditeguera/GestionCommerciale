export interface AuditLog {

  id: number;

  utilisateur: string;

  // 'CREATION' | 'MODIFICATION' | 'SUPPRESSION' (nom brut de l'enum backend TypeAction).
  action: string;

  entite: string;

  details: string;

  dateAction: string;

}
