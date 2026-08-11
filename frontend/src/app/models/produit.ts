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

  // Catégorie du produit (utilisée pour la ventilation des ventes par catégorie)
  categorie?: string;

  // Code-barres / QR code du produit, utilisé pour la recherche par scan caméra.
  codeBarre?: string;

}