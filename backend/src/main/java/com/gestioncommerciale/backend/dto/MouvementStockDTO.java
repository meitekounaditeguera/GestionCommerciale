package com.gestioncommerciale.backend.dto;

import java.time.LocalDateTime;

import com.gestioncommerciale.backend.model.TypeMouvement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStockDTO {

    private Long id;

    private Long produitId;

    private String produitNom;

    private Integer quantite;

    private TypeMouvement typeMouvement;

    private LocalDateTime dateMouvement;

    private String motif;

}
