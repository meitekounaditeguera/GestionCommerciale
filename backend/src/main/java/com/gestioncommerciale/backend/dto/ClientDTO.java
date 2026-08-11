package com.gestioncommerciale.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private Long id;
//NotBlank : Cette annotation est utilisée pour valider que le champ n'est pas vide ou null. Elle est souvent utilisée pour les champs de type String, où l'on souhaite s'assurer que l'utilisateur a fourni une valeur non vide. Si le champ est vide ou null, un message d'erreur personnalisé peut être affiché.
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(min = 10, max = 20, message = "Le téléphone doit contenir entre 10 et 20 caractères")
    private String telephone;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;
}