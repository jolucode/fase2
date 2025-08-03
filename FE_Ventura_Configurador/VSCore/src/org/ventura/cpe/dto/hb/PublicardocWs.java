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
import java.util.Date;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "PUBLICARDOC_WS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PublicardocWs.findAll", query = "SELECT p FROM PublicardocWs p"),
    @NamedQuery(name = "PublicardocWs.findHabilitadas", query = "SELECT p FROM PublicardocWs p WHERE p.estadoPublicacion =:estadoPublicacion AND p.fechaPublicacionPortal <= :fechaPublicacionPortal")})
public class PublicardocWs implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "FEId")
    private String fEId;
    @Basic(optional = false)
    @Column(name = "DOCId")
    private String dOCId;
    @Basic(optional = false)
    @Column(name = "FETipoTrans")
    private String fETipoTrans;
    @Basic(optional = false)
    @Column(name = "DOCFechaEmision")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dOCFechaEmision;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "DOCMontoTotal")
    private BigDecimal dOCMontoTotal;
    @Basic(optional = false)
    @Column(name = "DOCCodigo")
    private String dOCCodigo;
    @Basic(optional = false)
    @Column(name = "SNDocIdentidadNro")
    private String sNDocIdentidadNro;
    @Basic(optional = false)
    @Column(name = "SNRazonSocial")
    private String sNRazonSocial;
    @Basic(optional = false)
    @Column(name = "SNEMail")
    private String sNEMail;
    @Column(name = "SNEMailSecundario")
    private String sNEMailSecundario;
    @Lob
    @Column(name = "rutaPDF")
    private String rutaPDF;
    @Lob
    @Column(name = "rutaXML")
    private String rutaXML;
    @Lob
    @Column(name = "rutaZIP")
    private String rutaZIP;
    @Basic(optional = false)
    @Column(name = "EstadoSUNAT")
    private Character estadoSUNAT;
    @Basic(optional = false)
    @Column(name = "DOCMONCodigo")
    private String dOCMONCodigo;
    @Basic(optional = false)
    @Column(name = "DOCMONNombre")
    private String dOCMONNombre;
    @Basic(optional = false)
    @Column(name = "EMailEmisor")
    private String eMailEmisor;
    @Basic(optional = false)
    @Column(name = "EstadoPublicacion")
    private Character estadoPublicacion;
    @Basic(optional = false)
    @Column(name = "fechaPublicacionPortal")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacionPortal;

    public PublicardocWs() {
    }

    public PublicardocWs(String fEId) {
        this.fEId = fEId;
    }

    public PublicardocWs(String fEId, String dOCId, String fETipoTrans, Date dOCFechaEmision, BigDecimal dOCMontoTotal, String dOCCodigo, String sNDocIdentidadNro, String sNRazonSocial, String sNEMail, Character estadoSUNAT, String dOCMONCodigo, String dOCMONNombre, String eMailEmisor, Character estadoPublicacion, Date fechaPublicacionPortal) {
        this.fEId = fEId;
        this.dOCId = dOCId;
        this.fETipoTrans = fETipoTrans;
        this.dOCFechaEmision = dOCFechaEmision;
        this.dOCMontoTotal = dOCMontoTotal;
        this.dOCCodigo = dOCCodigo;
        this.sNDocIdentidadNro = sNDocIdentidadNro;
        this.sNRazonSocial = sNRazonSocial;
        this.sNEMail = sNEMail;
        this.estadoSUNAT = estadoSUNAT;
        this.dOCMONCodigo = dOCMONCodigo;
        this.dOCMONNombre = dOCMONNombre;
        this.eMailEmisor = eMailEmisor;
        this.estadoPublicacion = estadoPublicacion;
        this.fechaPublicacionPortal = fechaPublicacionPortal;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public String getDOCId() {
        return dOCId;
    }

    public void setDOCId(String dOCId) {
        this.dOCId = dOCId;
    }

    public String getFETipoTrans() {
        return fETipoTrans;
    }

    public void setFETipoTrans(String fETipoTrans) {
        this.fETipoTrans = fETipoTrans;
    }

    public Date getDOCFechaEmision() {
        return dOCFechaEmision;
    }

    public void setDOCFechaEmision(Date dOCFechaEmision) {
        this.dOCFechaEmision = dOCFechaEmision;
    }

    public BigDecimal getDOCMontoTotal() {
        return dOCMontoTotal;
    }

    public void setDOCMontoTotal(BigDecimal dOCMontoTotal) {
        this.dOCMontoTotal = dOCMontoTotal;
    }

    public String getDOCCodigo() {
        return dOCCodigo;
    }

    public void setDOCCodigo(String dOCCodigo) {
        this.dOCCodigo = dOCCodigo;
    }

    public String getSNDocIdentidadNro() {
        return sNDocIdentidadNro;
    }

    public void setSNDocIdentidadNro(String sNDocIdentidadNro) {
        this.sNDocIdentidadNro = sNDocIdentidadNro;
    }

    public String getSNRazonSocial() {
        return sNRazonSocial;
    }

    public void setSNRazonSocial(String sNRazonSocial) {
        this.sNRazonSocial = sNRazonSocial;
    }

    public String getSNEMail() {
        return sNEMail;
    }

    public void setSNEMail(String sNEMail) {
        this.sNEMail = sNEMail;
    }

    public String getSNEMailSecundario() {
        return sNEMailSecundario;
    }

    public void setSNEMailSecundario(String sNEMailSecundario) {
        this.sNEMailSecundario = sNEMailSecundario;
    }

    public String getRutaPDF() {
        return rutaPDF;
    }

    public void setRutaPDF(String rutaPDF) {
        this.rutaPDF = rutaPDF;
    }

    public String getRutaXML() {
        return rutaXML;
    }

    public void setRutaXML(String rutaXML) {
        this.rutaXML = rutaXML;
    }

    public String getRutaZIP() {
        return rutaZIP;
    }

    public void setRutaZIP(String rutaZIP) {
        this.rutaZIP = rutaZIP;
    }

    public Character getEstadoSUNAT() {
        return estadoSUNAT;
    }

    public void setEstadoSUNAT(Character estadoSUNAT) {
        this.estadoSUNAT = estadoSUNAT;
    }

    public String getDOCMONCodigo() {
        return dOCMONCodigo;
    }

    public void setDOCMONCodigo(String dOCMONCodigo) {
        this.dOCMONCodigo = dOCMONCodigo;
    }

    public String getDOCMONNombre() {
        return dOCMONNombre;
    }

    public void setDOCMONNombre(String dOCMONNombre) {
        this.dOCMONNombre = dOCMONNombre;
    }

    public String getEMailEmisor() {
        return eMailEmisor;
    }

    public void setEMailEmisor(String eMailEmisor) {
        this.eMailEmisor = eMailEmisor;
    }

    public Character getEstadoPublicacion() {
        return estadoPublicacion;
    }

    public void setEstadoPublicacion(Character estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }

    public Date getFechaPublicacionPortal() {
        return fechaPublicacionPortal;
    }

    public void setFechaPublicacionPortal(Date fechaPublicacionPortal) {
        this.fechaPublicacionPortal = fechaPublicacionPortal;
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
        if (!(object instanceof PublicardocWs)) {
            return false;
        }
        PublicardocWs other = (PublicardocWs) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.PublicardocWs[ fEId=" + fEId + " ]";
    }
    
}
