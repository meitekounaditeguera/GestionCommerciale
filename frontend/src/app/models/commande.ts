import { LigneCommande } from './lignecommande';

export interface Commande {

  id?: number;

  dateCommande: string;

  clientId: number;

  montantTotal?: number;

  lignes: LigneCommande[];

}