package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

public class ChiffreAffairesDTO {

    private BigDecimal journalier;
    private BigDecimal hebdomadaire;
    private BigDecimal mensuel;
    private BigDecimal annuel;

    public ChiffreAffairesDTO() {
    }

    public ChiffreAffairesDTO(BigDecimal journalier, BigDecimal hebdomadaire, BigDecimal mensuel, BigDecimal annuel) {
        this.journalier = journalier;
        this.hebdomadaire = hebdomadaire;
        this.mensuel = mensuel;
        this.annuel = annuel;
    }

    public BigDecimal getJournalier() {
        return journalier;
    }

    public void setJournalier(BigDecimal journalier) {
        this.journalier = journalier;
    }

    public BigDecimal getHebdomadaire() {
        return hebdomadaire;
    }

    public void setHebdomadaire(BigDecimal hebdomadaire) {
        this.hebdomadaire = hebdomadaire;
    }

    public BigDecimal getMensuel() {
        return mensuel;
    }

    public void setMensuel(BigDecimal mensuel) {
        this.mensuel = mensuel;
    }

    public BigDecimal getAnnuel() {
        return annuel;
    }

    public void setAnnuel(BigDecimal annuel) {
        this.annuel = annuel;
    }
}
