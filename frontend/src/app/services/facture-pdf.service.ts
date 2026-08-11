import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

import { Commande } from '../models/commande';
import { Client } from '../models/client';
import { Produit } from '../models/produit';

// Identité visuelle reprise à l'identique de la génération PDF côté backend
// (PdfFactureServiceImpl), pour que toutes les factures partagent le même en-tête.
const ENTREPRISE_NOM = 'TechShop SARL';
const ENTREPRISE_ADRESSE = "Abidjan, Abobo Plaque Anador, Côte d'Ivoire";
const ENTREPRISE_TELEPHONE = '+225 07 00 00 00 00';
const ENTREPRISE_EMAIL = 'contact@techshop.ci';

// Bleu corporate déjà utilisé dans le reste de l'application (graphiques du dashboard).
const COULEUR_BLEU_CORPORATE: [number, number, number] = [42, 78, 120];
const COULEUR_BORDURE: [number, number, number] = [200, 208, 218];
const COULEUR_GRISE: [number, number, number] = [90, 90, 90];

const MARGE = 15;

// Largeurs de colonnes du tableau des articles, en mm. Leur somme (180mm) correspond
// exactement à la largeur imprimable d'une page A4 (210mm - 15mm de marge de chaque côté),
// avec assez de place pour un nom de produit long et des montants du type "1 200 000 FCFA".
const LARGEUR_COL_PRODUIT = 60;
const LARGEUR_COL_QUANTITE = 30;
const LARGEUR_COL_PRIX_UNITAIRE = 40;
const LARGEUR_COL_TOTAL = 50;

@Injectable({
  providedIn: 'root'
})
export class FacturePdfService {

  // Génère la facture PDF d'une commande (format A4) et déclenche son téléchargement.
  // `client` et `produits` proviennent des listes déjà chargées côté composant : la
  // génération se fait entièrement dans le navigateur, sans appel réseau supplémentaire.
  genererFacturePDF(commande: Commande, client: Client | undefined, produits: Produit[]): void {

    const doc = new jsPDF({ unit: 'mm', format: 'a4' });
    const margeDroite = doc.internal.pageSize.getWidth() - MARGE;

    this.ajouterEnTete(doc, commande, margeDroite);
    const yApresClient = this.ajouterInfosClient(doc, client, 60);

    const lignes = commande.lignes.map(ligne => {
      const produit = produits.find(p => p.id === ligne.produitId);
      const prixUnitaire = ligne.prixUnitaire ?? produit?.prix ?? 0;
      const total = prixUnitaire * ligne.quantite;
      return [
        produit?.nom ?? 'Produit inconnu',
        String(ligne.quantite),
        this.formaterMontant(prixUnitaire),
        this.formaterMontant(total)
      ];
    });

    autoTable(doc, {
      startY: yApresClient + 6,
      margin: { left: MARGE, right: MARGE },
      tableWidth: margeDroite - MARGE,
      theme: 'grid',
      head: [['Produit', 'Quantité', 'Prix unitaire', 'Total']],
      body: lignes,
      styles: {
        fontSize: 10,
        cellPadding: 4,
        lineColor: COULEUR_BORDURE,
        lineWidth: 0.2,
        overflow: 'linebreak',
        valign: 'middle'
      },
      // fontStyle 'normal' (et non 'bold') : la police standard de jsPDF positionne mal
      // l'accent des caractères accentués en gras (ex: le "é" de "Quantité" apparaissait
      // décalé vers le bas). Le fond bleu + texte blanc suffisent à distinguer l'en-tête.
      // halign/valign 'center' : les 4 titres sont centrés horizontalement ET verticalement
      // dans le bandeau bleu (explicite ici pour ne pas dépendre de l'héritage de `styles`).
      headStyles: {
        fillColor: COULEUR_BLEU_CORPORATE,
        textColor: 255,
        fontStyle: 'normal',
        fontSize: 10.5,
        halign: 'center',
        valign: 'middle'
      },
      columnStyles: {
        0: { cellWidth: LARGEUR_COL_PRODUIT, halign: 'left' },
        // La quantité (ex: "5") est centrée sous le titre "Quantité", lui-même centré :
        // l'alignement colonne/valeur reste symétrique quelle que soit la largeur.
        1: { cellWidth: LARGEUR_COL_QUANTITE, halign: 'center', valign: 'middle' },
        2: { cellWidth: LARGEUR_COL_PRIX_UNITAIRE, halign: 'right' },
        3: { cellWidth: LARGEUR_COL_TOTAL, halign: 'right' }
      }
    });

    const finTableau = (doc as any).lastAutoTable?.finalY ?? (yApresClient + 20);
    this.ajouterTotal(doc, commande, margeDroite, finTableau);
    this.ajouterPiedDePage(doc, margeDroite);

    doc.save(`facture_commande_${commande.id}.pdf`);
  }

  private ajouterEnTete(doc: jsPDF, commande: Commande, margeDroite: number): void {

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(16);
    doc.setTextColor(...COULEUR_BLEU_CORPORATE);
    doc.text(ENTREPRISE_NOM, MARGE, 20);

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(...COULEUR_GRISE);
    doc.text(ENTREPRISE_ADRESSE, MARGE, 26);
    doc.text(`Tél : ${ENTREPRISE_TELEPHONE}`, MARGE, 31);
    doc.text(`Email : ${ENTREPRISE_EMAIL}`, MARGE, 36);

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(20);
    doc.setTextColor(0, 0, 0);
    doc.text('FACTURE', margeDroite, 20, { align: 'right' });

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    doc.setTextColor(60, 60, 60);
    doc.text(`N° ${this.genererNumeroFacture(commande)}`, margeDroite, 27, { align: 'right' });
    doc.text(`Date de facturation : ${this.formaterDate(new Date())}`, margeDroite, 32, { align: 'right' });
    if (commande.dateCommande) {
      doc.text(`Date de la commande : ${this.formaterDate(new Date(commande.dateCommande))}`, margeDroite, 37, { align: 'right' });
    }

    doc.setDrawColor(...COULEUR_BLEU_CORPORATE);
    doc.setLineWidth(0.6);
    doc.line(MARGE, 42, margeDroite, 42);
  }

  private ajouterInfosClient(doc: jsPDF, client: Client | undefined, y: number): number {

    // Poids 'normal' plutôt que 'bold' pour la même raison que l'en-tête du tableau :
    // la police standard de jsPDF déplace l'accent des caractères accentués en gras.
    // La couleur bleue corporate suffit à distinguer le libellé de section.
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(11);
    doc.setTextColor(...COULEUR_BLEU_CORPORATE);
    doc.text('Facturé à :', MARGE, y);

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    doc.setTextColor(60, 60, 60);

    if (!client) {
      doc.text('Client non renseigné', MARGE, y + 6);
      return y + 6;
    }

    doc.text(`${client.nom} ${client.prenom}`, MARGE, y + 6);
    doc.text(`Email : ${client.email || '—'}`, MARGE, y + 11);
    doc.text(`Téléphone : ${client.telephone || '—'}`, MARGE, y + 16);

    return y + 16;
  }

  // Affiche le montant total en gras, aligné à droite, juste sous le tableau,
  // séparé par un léger filet horizontal pour bien le détacher des lignes d'articles.
  private ajouterTotal(doc: jsPDF, commande: Commande, margeDroite: number, finTableau: number): void {

    const yFilet = finTableau + 8;

    doc.setDrawColor(...COULEUR_BLEU_CORPORATE);
    doc.setLineWidth(0.4);
    doc.line(margeDroite - LARGEUR_COL_TOTAL - LARGEUR_COL_PRIX_UNITAIRE, yFilet, margeDroite, yFilet);

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.setTextColor(0, 0, 0);
    doc.text(
      `Montant total : ${this.formaterMontant(commande.montantTotal ?? 0)}`,
      margeDroite,
      yFilet + 8,
      { align: 'right' }
    );
  }

  private ajouterPiedDePage(doc: jsPDF, margeDroite: number): void {
    const hauteurPage = doc.internal.pageSize.getHeight();

    doc.setDrawColor(220, 220, 220);
    doc.setLineWidth(0.3);
    doc.line(MARGE, hauteurPage - 20, margeDroite, hauteurPage - 20);

    doc.setFont('helvetica', 'italic');
    doc.setFontSize(9);
    doc.setTextColor(...COULEUR_GRISE);
    doc.text('Merci pour votre confiance !', (MARGE + margeDroite) / 2, hauteurPage - 14, { align: 'center' });
  }

  // Reproduit le même format de numéro de facture que le backend et l'export Excel
  // (FAC-{année}-{id sur 4 chiffres}), pour une numérotation cohérente dans toute l'application.
  private genererNumeroFacture(commande: Commande): string {
    const annee = commande.dateCommande ? commande.dateCommande.split('-')[0] : new Date().getFullYear().toString();
    const idFormatte = String(commande.id ?? 0).padStart(4, '0');
    return `FAC-${annee}-${idFormatte}`;
  }

  private formaterDate(date: Date): string {
    return date.toLocaleDateString('fr-FR');
  }

  // Regroupe les milliers avec un espace ASCII standard. `toLocaleString('fr-FR')` insère
  // une espace fine insécable (U+202F) que la police standard de jsPDF ne sait pas
  // représenter : elle s'affichait comme un caractère parasite (ex: "1/200/000").
  private formaterMontant(montant: number): string {

    const arrondi = Math.round(montant);
    const signe = arrondi < 0 ? '-' : '';
    const chiffres = Math.abs(arrondi).toString();

    const groupes: string[] = [];
    for (let fin = chiffres.length; fin > 0; fin -= 3) {
      groupes.unshift(chiffres.substring(Math.max(0, fin - 3), fin));
    }

    return `${signe}${groupes.join(' ')} FCFA`;
  }
}
