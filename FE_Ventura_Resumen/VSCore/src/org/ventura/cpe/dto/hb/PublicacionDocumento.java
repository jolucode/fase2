/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "PUBLICACION_DOCUMENTO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PublicacionDocumento.findAll", query = "SELECT p FROM PublicacionDocumento p"),
    @NamedQuery(name = "PublicacionDocumento.findByFEId", query = "SELECT p FROM PublicacionDocumento p WHERE p.fEId = :fEId"),
    @NamedQuery(name = "PublicacionDocumento.findByNombreConsumidor", query = "SELECT p FROM PublicacionDocumento p WHERE p.nombreConsumidor = :nombreConsumidor"),
    @NamedQuery(name = "PublicacionDocumento.findByEmailConsumidor", query = "SELECT p FROM PublicacionDocumento p WHERE p.emailConsumidor = :emailConsumidor"),
    @NamedQuery(name = "PublicacionDocumento.findByNumeroSerie", query = "SELECT p FROM PublicacionDocumento p WHERE p.numeroSerie = :numeroSerie"),
    @NamedQuery(name = "PublicacionDocumento.findByTipoDocumento", query = "SELECT p FROM PublicacionDocumento p WHERE p.tipoDocumento = :tipoDocumento"),
    @NamedQuery(name = "PublicacionDocumento.findByTotal", query = "SELECT p FROM PublicacionDocumento p WHERE p.total = :total"),
    @NamedQuery(name = "PublicacionDocumento.findByEstadoSunat", query = "SELECT p FROM PublicacionDocumento p WHERE p.estadoSunat = :estadoSunat"),
    @NamedQuery(name = "PublicacionDocumento.findByMoneda", query = "SELECT p FROM PublicacionDocumento p WHERE p.moneda = :moneda"),
    @NamedQuery(name = "PublicacionDocumento.findByEmailEmisor", query = "SELECT p FROM PublicacionDocumento p WHERE p.emailEmisor = :emailEmisor"),
    @NamedQuery(name = "PublicacionDocumento.findByRutaPdf", query = "SELECT p FROM PublicacionDocumento p WHERE p.rutaPdf = :rutaPdf"),
    @NamedQuery(name = "PublicacionDocumento.findByRutaXml", query = "SELECT p FROM PublicacionDocumento p WHERE p.rutaXml = :rutaXml"),
    @NamedQuery(name = "PublicacionDocumento.findByRutaCdr", query = "SELECT p FROM PublicacionDocumento p WHERE p.rutaCdr = :rutaCdr"),
    @NamedQuery(name = "PublicacionDocumento.findByEstado", query = "SELECT p FROM PublicacionDocumento p WHERE p.estado = :estado"),
    @NamedQuery(name = "PublicacionDocumento.findByMsjError", query = "SELECT p FROM PublicacionDocumento p WHERE p.msjError = :msjError"),
    @NamedQuery(name = "PublicacionDocumento.findByRuc", query = "SELECT p FROM PublicacionDocumento p WHERE p.ruc = :ruc"),
    @NamedQuery(name = "PublicacionDocumento.findByFechaEmision", query = "SELECT p FROM PublicacionDocumento p WHERE p.fechaEmision = :fechaEmision")})
public class PublicacionDocumento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;
    @Column(name = "nombreConsumidor")
    private String nombreConsumidor;
    @Column(name = "emailConsumidor")
    private String emailConsumidor;
    @Column(name = "numeroSerie")
    private String numeroSerie;
    @Column(name = "TipoDocumento")
    private String tipoDocumento;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Total")
    private BigDecimal total;
    @Column(name = "estadoSunat")
    private String estadoSunat;
    @Column(name = "moneda")
    private String moneda;
    @Column(name = "emailEmisor")
    private String emailEmisor;
    @Column(name = "Ruta_Pdf")
    private String rutaPdf;
    @Column(name = "Ruta_Xml")
    private String rutaXml;
    @Column(name = "Ruta_Cdr")
    private String rutaCdr;
    @Column(name = "Estado")
    private Integer estado;
    @Column(name = "Msj_Error")
    private String msjError;
    @Column(name = "Ruc")
    private String ruc;
    @Column(name = "FechaEmision")
    private String fechaEmision;

    public PublicacionDocumento() {
    }

    public PublicacionDocumento(String fEId) {
        this.fEId = fEId;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public String getNombreConsumidor() {
        return nombreConsumidor;
    }

    public void setNombreConsumidor(String nombreConsumidor) {
        this.nombreConsumidor = nombreConsumidor;
    }

    public String getEmailConsumidor() {
        return emailConsumidor;
    }

    public void setEmailConsumidor(String emailConsumidor) {
        this.emailConsumidor = emailConsumidor;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstadoSunat() {
        return estadoSunat;
    }

    public void setEstadoSunat(String estadoSunat) {
        this.estadoSunat = estadoSunat;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getEmailEmisor() {
        return emailEmisor;
    }

    public void setEmailEmisor(String emailEmisor) {
        this.emailEmisor = emailEmisor;
    }

    public String getRutaPdf() {
        return rutaPdf;
    }

    public void setRutaPdf(String rutaPdf) {
        this.rutaPdf = rutaPdf;
    }

    public String getRutaXml() {
        return rutaXml;
    }

    public void setRutaXml(String rutaXml) {
        this.rutaXml = rutaXml;
    }

    public String getRutaCdr() {
        return rutaCdr;
    }

    public void setRutaCdr(String rutaCdr) {
        this.rutaCdr = rutaCdr;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getMsjError() {
        return msjError;
    }

    public void setMsjError(String msjError) {
        this.msjError = msjError;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEId != null ? fEId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PublicacionDocumento)) {
            return false;
        }
        PublicacionDocumento other = (PublicacionDocumento) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.PublicacionDocumento[ fEId=" + fEId + " ]";
    }
    
}
