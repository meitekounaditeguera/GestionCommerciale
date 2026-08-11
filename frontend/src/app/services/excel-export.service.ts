import { Injectable } from '@angular/core';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

// Couleur de fond de l'en-tête (bleu nuit) et texte blanc, au format ARGB attendu par ExcelJS.
const COULEUR_ENTETE = 'FF1E293B';
const COULEUR_TEXTE_ENTETE = 'FFFFFFFF';

// Marge ajoutée à la largeur de colonne calculée automatiquement, pour éviter que le
// contenu ne touche les bords de la cellule.
const MARGE_LARGEUR_COLONNE = 4;

@Injectable({
  providedIn: 'root'
})
export class ExcelExportService {

  // Génère un classeur .xlsx à partir d'un jeu de données tabulaire et déclenche son
  // téléchargement. `data` est un tableau de lignes, chaque ligne étant un tableau de
  // valeurs dans le même ordre que `headers`.
  async exportToExcel(headers: string[], data: any[][], fileName: string, sheetName: string): Promise<void> {

    const workbook = new ExcelJS.Workbook();
    const feuille = workbook.addWorksheet(sheetName);

    const ligneEntete = feuille.addRow(headers);
    ligneEntete.eachCell((cellule) => {
      cellule.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COULEUR_ENTETE }
      };
      cellule.font = {
        color: { argb: COULEUR_TEXTE_ENTETE },
        bold: true
      };
      cellule.alignment = {
        horizontal: 'center',
        vertical: 'middle'
      };
    });

    data.forEach(ligne => feuille.addRow(ligne));

    // Largeur automatique des colonnes, calculée à partir du contenu le plus long
    // (en-tête compris) sur chaque colonne.
    headers.forEach((entete, index) => {
      let largeurMax = entete ? entete.toString().length : 10;

      data.forEach(ligne => {
        const valeur = ligne[index];
        const longueur = valeur !== null && valeur !== undefined ? valeur.toString().length : 0;
        if (longueur > largeurMax) {
          largeurMax = longueur;
        }
      });

      feuille.getColumn(index + 1).width = largeurMax + MARGE_LARGEUR_COLONNE;
    });

    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });

    saveAs(blob, `${fileName}_${this.suffixeDateDuJour()}.xlsx`);
  }

  private suffixeDateDuJour(): string {
    const aujourdHui = new Date();
    const annee = aujourdHui.getFullYear();
    const mois = String(aujourdHui.getMonth() + 1).padStart(2, '0');
    const jour = String(aujourdHui.getDate()).padStart(2, '0');
    return `${annee}-${mois}-${jour}`;
  }

}
