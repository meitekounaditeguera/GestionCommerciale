package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeDTO {

    private Long id;

    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être supérieure ou égale à 1")
    private Integer quantite;

    // Calculé côté serveur à partir du prix du produit : jamais fourni par le client.
    private BigDecimal prixUnitaire;

}
