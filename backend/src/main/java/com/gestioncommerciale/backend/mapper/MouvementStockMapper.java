package com.gestioncommerciale.backend.mapper;

import com.gestioncommerciale.backend.dto.MouvementStockDTO;
import com.gestioncommerciale.backend.model.MouvementStock;
import com.gestioncommerciale.backend.model.TypeMouvement;

public class MouvementStockMapper {

    public static MouvementStockDTO toDTO(MouvementStock mouvement) {

        if (mouvement == null) {
            return null;
        }

        MouvementStockDTO dto = new MouvementStockDTO();

        dto.setId(mouvement.getId());
        dto.setQuantite(mouvement.getQuantite());
        dto.setTypeMouvement(mouvement.getTypeMouvement());
        dto.setDateMouvement(mouvement.getDateMouvement());
        dto.setMotif(motifOuDefaut(mouvement));

        if (mouvement.getProduit() != null) {
            dto.setProduitId(mouvement.getProduit().getId());
            dto.setProduitNom(mouvement.getProduit().getNom());
        } else {
            dto.setProduitNom("Produit inconnu");
        }

        return dto;
    }

    // Évite un motif vide à l'affichage : "Réapprovisionnement" pour une entrée,
    // "Vente" pour une sortie, quand aucun motif n'a été saisi.
    private static String motifOuDefaut(MouvementStock mouvement) {

        if (mouvement.getMotif() != null && !mouvement.getMotif().isBlank()) {
            return mouvement.getMotif();
        }

        return mouvement.getTypeMouvement() == TypeMouvement.ENTREE ? "Réapprovisionnement" : "Vente";
    }

}
