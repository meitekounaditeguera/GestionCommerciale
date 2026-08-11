package com.gestioncommerciale.backend.dto;

import java.math.BigDecimal;

public class DashboardStatsDTO {

    private long totalClients;
    private long totalProduits;
    private long totalCommandes;
    private BigDecimal chiffreAffaires;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(long totalClients, long totalProduits, long totalCommandes, BigDecimal chiffreAffaires) {
        this.totalClients = totalClients;
        this.totalProduits = totalProduits;
        this.totalCommandes = totalCommandes;
        this.chiffreAffaires = chiffreAffaires;
    }

    public long getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(long totalClients) {
        this.totalClients = totalClients;
    }

    public long getTotalProduits() {
        return totalProduits;
    }

    public void setTotalProduits(long totalProduits) {
        this.totalProduits = totalProduits;
    }

    public long getTotalCommandes() {
        return totalCommandes;
    }

    public void setTotalCommandes(long totalCommandes) {
        this.totalCommandes = totalCommandes;
    }

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }
}
