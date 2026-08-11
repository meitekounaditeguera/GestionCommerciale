package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeFournisseurDTO {

    private Long id;

    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être supérieure ou égale à 1")
    private Integer quantite;

    // Prix d'achat négocié avec le fournisseur : à la différence d'une vente,
    // il n'est jamais déduit du prix de vente du produit et doit être fourni par le client.
    @NotNull(message = "Le prix d'achat unitaire est obligatoire")
    @DecimalMin(value = "0", message = "Le prix d'achat unitaire doit être supérieur ou égal à 0")
    private BigDecimal prixAchatUnitaire;

}
