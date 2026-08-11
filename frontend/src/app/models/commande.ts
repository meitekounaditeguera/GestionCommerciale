import { LigneCommande } from './lignecommande';

export interface Commande {

  id?: number;

  dateCommande: string;

  clientId: number;

  montantTotal?: number;

  // 'VALIDE' ou 'ANNULE', calculé côté serveur.
  statut?: string;

  lignes: LigneCommande[];

}