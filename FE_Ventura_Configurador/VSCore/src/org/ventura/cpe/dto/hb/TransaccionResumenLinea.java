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
@Table(name = "TRANSACCION_RESUMEN_LINEA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionResumenLinea.findAll", query = "SELECT t FROM TransaccionResumenLinea t"),
    @NamedQuery(name = "TransaccionResumenLinea.findByIdTransaccion", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.transaccionResumenLineaPK.idTransaccion = :idTransaccion"),
    @NamedQuery(name = "TransaccionResumenLinea.findByIdLinea", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.transaccionResumenLineaPK.idLinea = :idLinea"),
    @NamedQuery(name = "TransaccionResumenLinea.findByTipoDocumento", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.tipoDocumento = :tipoDocumento"),
    @NamedQuery(name = "TransaccionResumenLinea.findByNumeroSerie", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.numeroSerie = :numeroSerie"),
    @NamedQuery(name = "TransaccionResumenLinea.findByNumeroCorrelativoInicio", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.numeroCorrelativoInicio = :numeroCorrelativoInicio"),
    @NamedQuery(name = "TransaccionResumenLinea.findByNumeroCorrelativoFin", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.numeroCorrelativoFin = :numeroCorrelativoFin"),
    @NamedQuery(name = "TransaccionResumenLinea.findByTotalOPGravadas", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.totalOPGravadas = :totalOPGravadas"),
    @NamedQuery(name = "TransaccionResumenLinea.findByTotalOPExoneradas", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.totalOPExoneradas = :totalOPExoneradas"),
    @NamedQuery(name = "TransaccionResumenLinea.findByTotalOPInafectas", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.totalOPInafectas = :totalOPInafectas"),
    @NamedQuery(name = "TransaccionResumenLinea.findByImporteOtrosCargos", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.importeOtrosCargos = :importeOtrosCargos"),
    @NamedQuery(name = "TransaccionResumenLinea.findByTotalISC", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.totalISC = :totalISC"),
    @NamedQuery(name = "TransaccionResumenLinea.findByTotaIGV", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.totaIGV = :totaIGV"),
    @NamedQuery(name = "TransaccionResumenLinea.findByTotalOtrosTributos", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.totalOtrosTributos = :totalOtrosTributos"),
    @NamedQuery(name = "TransaccionResumenLinea.findByImporteTotal", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.importeTotal = :importeTotal"),
    @NamedQuery(name = "TransaccionResumenLinea.findByCodMoneda", query = "SELECT t FROM TransaccionResumenLinea t WHERE t.codMoneda = :codMoneda")})
public class TransaccionResumenLinea implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionResumenLineaPK transaccionResumenLineaPK;
    @Column(name = "Tipo_Documento")
    private String tipoDocumento;
    @Column(name = "Numero_Serie")
    private String numeroSerie;
    @Column(name = "Numero_Correlativo_Inicio")
    private String numeroCorrelativoInicio;
    @Column(name = "Numero_Correlativo_Fin")
    private String numeroCorrelativoFin;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Total_OP_Gravadas")
    private BigDecimal totalOPGravadas;
    @Column(name = "Total_OP_Exoneradas")
    private BigDecimal totalOPExoneradas;
    @Column(name = "Total_OP_Inafectas")
    private BigDecimal totalOPInafectas;
    @Column(name = "Importe_Otros_Cargos")
    private BigDecimal importeOtrosCargos;
    @Column(name = "Total_ISC")
    private BigDecimal totalISC;
    @Column(name = "Tota_IGV")
    private BigDecimal totaIGV;
    @Column(name = "Total_Otros_Tributos")
    private BigDecimal totalOtrosTributos;
    @Column(name = "Importe_Total")
    private BigDecimal importeTotal;
    @Column(name = "CodMoneda")
    private String codMoneda;
    @JoinColumn(name = "Id_Transaccion", referencedColumnName = "Id_Transaccion", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TransaccionResumen transaccionResumen;

    public TransaccionResumenLinea() {
    }

    public TransaccionResumenLinea(TransaccionResumenLineaPK transaccionResumenLineaPK) {
        this.transaccionResumenLineaPK = transaccionResumenLineaPK;
    }

    public TransaccionResumenLinea(String idTransaccion, int idLinea) {
        this.transaccionResumenLineaPK = new TransaccionResumenLineaPK(idTransaccion, idLinea);
    }

    public TransaccionResumenLineaPK getTransaccionResumenLineaPK() {
        return transaccionResumenLineaPK;
    }

    public void setTransaccionResumenLineaPK(TransaccionResumenLineaPK transaccionResumenLineaPK) {
        this.transaccionResumenLineaPK = transaccionResumenLineaPK;
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

    public String getNumeroCorrelativoInicio() {
        return numeroCorrelativoInicio;
    }

    public void setNumeroCorrelativoInicio(String numeroCorrelativoInicio) {
        this.numeroCorrelativoInicio = numeroCorrelativoInicio;
    }

    public String getNumeroCorrelativoFin() {
        return numeroCorrelativoFin;
    }

    public void setNumeroCorrelativoFin(String numeroCorrelativoFin) {
        this.numeroCorrelativoFin = numeroCorrelativoFin;
    }

    public BigDecimal getTotalOPGravadas() {
        return totalOPGravadas;
    }

    public void setTotalOPGravadas(BigDecimal totalOPGravadas) {
        this.totalOPGravadas = totalOPGravadas;
    }

    public BigDecimal getTotalOPExoneradas() {
        return totalOPExoneradas;
    }

    public void setTotalOPExoneradas(BigDecimal totalOPExoneradas) {
        this.totalOPExoneradas = totalOPExoneradas;
    }

    public BigDecimal getTotalOPInafectas() {
        return totalOPInafectas;
    }

    public void setTotalOPInafectas(BigDecimal totalOPInafectas) {
        this.totalOPInafectas = totalOPInafectas;
    }

    public BigDecimal getImporteOtrosCargos() {
        return importeOtrosCargos;
    }

    public void setImporteOtrosCargos(BigDecimal importeOtrosCargos) {
        this.importeOtrosCargos = importeOtrosCargos;
    }

    public BigDecimal getTotalISC() {
        return totalISC;
    }

    public void setTotalISC(BigDecimal totalISC) {
        this.totalISC = totalISC;
    }

    public BigDecimal getTotaIGV() {
        return totaIGV;
    }

    public void setTotaIGV(BigDecimal totaIGV) {
        this.totaIGV = totaIGV;
    }

    public BigDecimal getTotalOtrosTributos() {
        return totalOtrosTributos;
    }

    public void setTotalOtrosTributos(BigDecimal totalOtrosTributos) {
        this.totalOtrosTributos = totalOtrosTributos;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getCodMoneda() {
        return codMoneda;
    }

    public void setCodMoneda(String codMoneda) {
        this.codMoneda = codMoneda;
    }

    public TransaccionResumen getTransaccionResumen() {
        return transaccionResumen;
    }

    public void setTransaccionResumen(TransaccionResumen transaccionResumen) {
        this.transaccionResumen = transaccionResumen;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionResumenLineaPK != null ? transaccionResumenLineaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionResumenLinea)) {
            return false;
        }
        TransaccionResumenLinea other = (TransaccionResumenLinea) object;
        if ((this.transaccionResumenLineaPK == null && other.transaccionResumenLineaPK != null) || (this.transaccionResumenLineaPK != null && !this.transaccionResumenLineaPK.equals(other.transaccionResumenLineaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionResumenLinea[ transaccionResumenLineaPK=" + transaccionResumenLineaPK + " ]";
    }
    
}
