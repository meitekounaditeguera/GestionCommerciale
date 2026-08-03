package com.gestioncommerciale.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDTO {

    private Long id;

    // Nom obligatoire
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    // Description facultative
    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    // Prix obligatoire
    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être supérieur ou égal à 0")
    private BigDecimal prix;

    // Quantité obligatoire
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité doit être supérieure ou égale à 0")
    private Integer quantite;

}