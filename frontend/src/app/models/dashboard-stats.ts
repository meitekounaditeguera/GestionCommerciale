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

  // Estimation du nombre de jours avant épuisement, fondée sur la vélocité de vente réelle
  // du produit sur les 14 derniers jours (calculée côté backend). null = indéterminé (aucune
  // vente récente pour ce produit) : à afficher explicitement comme tel, pas comme "0 jour".
  joursAvantRupture: number | null;
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
