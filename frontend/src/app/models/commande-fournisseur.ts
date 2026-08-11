export type StatutCommandeFournisseur = 'BROUILLON' | 'VALIDEE' | 'LIVREE' | 'ANNULEE';

export interface LigneCommandeFournisseur {

  id?: number;

  produitId: number;

  quantite: number;

  // Prix d'achat négocié avec le fournisseur (distinct du prix de vente du produit).
  prixAchatUnitaire: number;

}

export interface CommandeFournisseur {

  id?: number;

  // Générée par le serveur (ex: CF-2026-0001), jamais fournie par le client.
  reference?: string;

  dateCommande: string;

  // Toujours BROUILLON à la création ; piloté ensuite par valider/annuler/recevoir.
  statut?: StatutCommandeFournisseur;

  fournisseurId: number;

  lignes: LigneCommandeFournisseur[];

  // Calculé côté serveur à partir des lignes.
  montantTotal?: number;

}
