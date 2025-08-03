package org.ventura.cpe.core.dto;

import java.time.LocalDate;

public class ReporteVentaSearch {

    private String keySociedad;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    public ReporteVentaSearch() {
    }

    public ReporteVentaSearch(String keySociedad, LocalDate fechaInicio, LocalDate fechaFin) {
        this.keySociedad = keySociedad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getKeySociedad() {
        return keySociedad;
    }

    public void setKeySociedad(String keySociedad) {
        this.keySociedad = keySociedad;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
