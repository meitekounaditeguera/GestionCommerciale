package com.gestioncommerciale.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Format unique des montants écrits en texte par le serveur (journal d'audit, facture PDF
// archivée) : "400 000 FCFA", sans décimales et milliers séparés par une espace, comme la
// facture générée côté client (facture-pdf.service.ts). Sans lui, BigDecimal.toString()
// donnait "400000.00" pour une vente (prix relus en base avec 2 décimales) et "1500000"
// pour un achat (prix saisis sans décimales).
public final class MontantFcfa {

    private MontantFcfa() {
    }

    public static String formater(BigDecimal montant) {
        BigDecimal arrondi = montant.setScale(0, RoundingMode.HALF_UP);
        String signe = arrondi.signum() < 0 ? "-" : "";
        String chiffres = arrondi.abs().toPlainString();

        StringBuilder groupes = new StringBuilder();
        for (int i = 0; i < chiffres.length(); i++) {
            if (i > 0 && (chiffres.length() - i) % 3 == 0) {
                groupes.append(' ');
            }
            groupes.append(chiffres.charAt(i));
        }

        return signe + groupes + " FCFA";
    }
}
