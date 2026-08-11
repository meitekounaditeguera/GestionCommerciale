package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

public class CaMensuelDTO {

    private String mois;
    private BigDecimal chiffreAffaires;

    public CaMensuelDTO() {
    }

    public CaMensuelDTO(String mois, BigDecimal chiffreAffaires) {
        this.mois = mois;
        this.chiffreAffaires = chiffreAffaires;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }
}
