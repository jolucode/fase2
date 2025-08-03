package org.ventura.cpe.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteVenta {

    private String tipoDocumento;

    private String nombre;

    private String numeroSerie;

    private BigDecimal totalDocumento;

    private LocalDate fecha;

    private String rucCliente;

    private String respuestaSunat;

    private String emisor;

    public ReporteVenta() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public BigDecimal getTotalDocumento() {
        return totalDocumento;
    }

    public void setTotalDocumento(BigDecimal totalDocumento) {
        this.totalDocumento = totalDocumento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public String getRespuestaSunat() {
        return respuestaSunat;
    }

    public void setRespuestaSunat(String respuestaSunat) {
        this.respuestaSunat = respuestaSunat;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    @Override
    public String toString() {
        return "ReporteVenta{" +
                "tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroSerie='" + numeroSerie + '\'' +
                ", totalDocumento=" + totalDocumento +
                ", nombre=" + nombre +
                ", fecha=" + fecha +
                ", rucCliente='" + rucCliente + '\'' +
                ", respuestaSunat='" + respuestaSunat + '\'' +
                ", emisor='" + emisor + '\'' +
                '}';
    }
}