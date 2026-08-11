export interface EntreeStock {
  produitId: number;
  quantite: number;
  motif?: string;
}

export interface MouvementStock {
  id: number;
  produitId: number;
  produitNom: string;
  quantite: number;
  typeMouvement: 'ENTREE' | 'SORTIE';
  dateMouvement: string;
  motif?: string;
}
