// Représente un produit de l'application.
export interface Produit {

  id?: number;

  // Nom du produit
  nom: string;

  // Description
  description: string;

  // Prix de vente
  prix: number;

  // Quantité disponible
  quantite: number;

}