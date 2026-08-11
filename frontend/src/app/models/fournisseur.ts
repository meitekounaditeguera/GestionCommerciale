export interface Fournisseur {

  id?: number;

  // Nom / raison sociale du fournisseur.
  nom: string;

  email: string;

  telephone: string;

  adresse?: string;

  // Renseignée automatiquement par le serveur.
  dateCreation?: string;

}
