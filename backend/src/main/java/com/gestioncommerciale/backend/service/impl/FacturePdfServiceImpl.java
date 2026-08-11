package com.gestioncommerciale.backend.service.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gestioncommerciale.backend.exception.FactureGenerationException;
import com.gestioncommerciale.backend.exception.FactureNotFoundException;
import com.gestioncommerciale.backend.model.Client;
import com.gestioncommerciale.backend.model.Commande;
import com.gestioncommerciale.backend.model.LigneCommande;
import com.gestioncommerciale.backend.service.FacturePdfService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

// Génère la facture PDF côté serveur et l'archive sur le disque : voir application.properties
// (facture.stockage.dossier) pour le compromis fichier-système vs base de données. Mise en
// page volontairement simple (pas de logo, pas d'identité visuelle) : l'objectif ici est un
// document fiable et archivable, pas la facture "vitrine" déjà générée côté client (jsPDF).
@Service
public class FacturePdfServiceImpl implements FacturePdfService {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);

    private final String dossierStockage;

    public FacturePdfServiceImpl(@Value("${facture.stockage.dossier}") String dossierStockage) {
        this.dossierStockage = dossierStockage;
    }

    @Override
    public void genererEtArchiver(Commande commande) {
        try {
            byte[] contenu = construirePdf(commande);
            Path chemin = cheminFacture(commande.getId());
            Files.createDirectories(chemin.getParent());
            Files.write(chemin, contenu);
        } catch (DocumentException | IOException ex) {
            throw new FactureGenerationException(
                    "Impossible de générer la facture PDF pour la commande #" + commande.getId(), ex);
        }
    }

    @Override
    public byte[] lireFacture(Long commandeId) {
        Path chemin = cheminFacture(commandeId);

        if (!Files.exists(chemin)) {
            throw new FactureNotFoundException(
                    "Aucune facture archivée pour la commande #" + commandeId
                            + " (commande créée avant l'introduction de cette fonctionnalité, "
                            + "ou génération initiale en échec).");
        }

        try {
            return Files.readAllBytes(chemin);
        } catch (IOException ex) {
            throw new FactureGenerationException("Impossible de lire la facture de la commande #" + commandeId, ex);
        }
    }

    private Path cheminFacture(Long commandeId) {
        return Paths.get(dossierStockage, "commande-" + commandeId + ".pdf");
    }

    private byte[] construirePdf(Commande commande) throws DocumentException {

        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, sortie);
        document.open();

        Font policeTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font policeEntete = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font policeNormale = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Client client = commande.getClient();

        document.add(new Paragraph("Facture - Commande #" + commande.getId(), policeTitre));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Client : " + client.getNom() + " " + client.getPrenom(), policeNormale));
        document.add(new Paragraph("Date : " + commande.getDateCommande().format(FORMAT_DATE), policeNormale));
        document.add(new Paragraph(" "));

        PdfPTable tableau = new PdfPTable(4);
        tableau.setWidthPercentage(100);
        tableau.setWidths(new float[] { 4, 1, 2, 2 });

        ajouterEntete(tableau, "Produit", policeEntete);
        ajouterEntete(tableau, "Quantité", policeEntete);
        ajouterEntete(tableau, "Prix unitaire", policeEntete);
        ajouterEntete(tableau, "Sous-total", policeEntete);

        for (LigneCommande ligne : commande.getLignesCommande()) {
            BigDecimal sousTotal = ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite()));

            tableau.addCell(new Phrase(ligne.getProduit().getNom(), policeNormale));
            tableau.addCell(new Phrase(String.valueOf(ligne.getQuantite()), policeNormale));
            tableau.addCell(new Phrase(ligne.getPrixUnitaire() + " FCFA", policeNormale));
            tableau.addCell(new Phrase(sousTotal + " FCFA", policeNormale));
        }

        document.add(tableau);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Montant total : " + commande.getMontantTotal() + " FCFA", policeEntete));

        document.close();
        return sortie.toByteArray();
    }

    private void ajouterEntete(PdfPTable tableau, String texte, Font police) {
        PdfPCell cellule = new PdfPCell(new Phrase(texte, police));
        cellule.setBackgroundColor(new Color(230, 230, 230));
        tableau.addCell(cellule);
    }
}
