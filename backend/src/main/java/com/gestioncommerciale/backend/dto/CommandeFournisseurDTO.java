package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.gestioncommerciale.backend.model.StatutCommandeFournisseur;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseurDTO {

    private Long id;

    // Générée par le serveur (ex: CF-2026-0001), jamais fournie par le client.
    private String reference;

    @NotNull(message = "La date de la commande est obligatoire")
    private LocalDate dateCommande;

    // Géré par le serveur : toujours BROUILLON à la création, ensuite piloté
    // par les endpoints valider / annuler / recevoir.
    private StatutCommandeFournisseur statut;

    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;

    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    @Valid
    private List<LigneCommandeFournisseurDTO> lignes;

    // Calculé côté serveur à partir des lignes : jamais fourni par le client.
    private BigDecimal montantTotal;

}
