export interface DashboardStats {
  totalClients: number;
  totalProduits: number;
  totalCommandes: number;
  chiffreAffaires: number;
}

export interface CaMensuel {
  mois: string;
  chiffreAffaires: number;
}

export interface VentesParCategorie {
  categorie: string;
  totalVentes: number;
}

export interface TopProduit {
  produitId: number;
  nom: string;
  quantiteVendue: number;
  chiffreAffaires: number;
}

export interface ProduitRupture {
  id: number;
  nom: string;
  quantite: number;
}

export interface NouveauxClients {
  nombre: number;
  periodeJours: number;
}

export interface ChiffreAffaires {
  journalier: number;
  hebdomadaire: number;
  mensuel: number;
  annuel: number;
}

export interface MeilleurClient {
  nom: string;
  prenom: string;
  totalDepense: number;
}

export interface ProduitPhare {
  nom: string;
  quantiteVendue: number;
}

export interface CategoriePopulaire {
  categorie: string;
  quantiteVendue: number;
}
