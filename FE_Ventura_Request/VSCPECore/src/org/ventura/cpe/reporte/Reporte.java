/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.reporte;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author VS-LT-06
 */
public class Reporte {

    private String tipoDocumento;

    private String numeroSerie;

    private BigDecimal totalDocumento;

    private Date Fecha;

    private String rucCliente;

    public String getIdEmisor() {
        return idEmisor;
    }

    private String idEmisor;

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getRespuestaSunat() {
        return respuestaSunat;
    }

    public void setRespuestaSunat(String respuestaSunat) {
        this.respuestaSunat = respuestaSunat;
    }

    private String respuestaSunat;

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date Fecha) {
        this.Fecha = Fecha;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
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

}
