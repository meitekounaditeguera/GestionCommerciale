package com.gestioncommerciale.backend.dto;

public class NouveauxClientsDTO {

    private long nombre;
    private int periodeJours;

    public NouveauxClientsDTO() {
    }

    public NouveauxClientsDTO(long nombre, int periodeJours) {
        this.nombre = nombre;
        this.periodeJours = periodeJours;
    }

    public long getNombre() {
        return nombre;
    }

    public void setNombre(long nombre) {
        this.nombre = nombre;
    }

    public int getPeriodeJours() {
        return periodeJours;
    }

    public void setPeriodeJours(int periodeJours) {
        this.periodeJours = periodeJours;
    }
}
