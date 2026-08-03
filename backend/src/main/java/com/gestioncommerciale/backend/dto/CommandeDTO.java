package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDTO {

    private Long id;

    @NotNull(message = "La date de la commande est obligatoire")
    private LocalDate dateCommande;

    // Calculé côté serveur à partir des lignes : jamais fourni par le client.
    private BigDecimal montantTotal;

    // Identifiant du client
    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    // Les lignes de la commande
    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    @Valid
    private List<LigneCommandeDTO> lignes;

}
