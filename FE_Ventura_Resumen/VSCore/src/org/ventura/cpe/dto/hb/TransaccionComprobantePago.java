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
@Table(name = "TRANSACCION_COMPROBANTE_PAGO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionComprobantePago.findAll", query = "SELECT t FROM TransaccionComprobantePago t"),
    @NamedQuery(name = "TransaccionComprobantePago.findByFEId", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.transaccionComprobantePagoPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionComprobantePago.findByNroOrden", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.transaccionComprobantePagoPK.nroOrden = :nroOrden"),
    @NamedQuery(name = "TransaccionComprobantePago.findByDOCTipo", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.dOCTipo = :dOCTipo"),
    @NamedQuery(name = "TransaccionComprobantePago.findByDOCNumero", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.dOCNumero = :dOCNumero"),
    @NamedQuery(name = "TransaccionComprobantePago.findByDOCFechaEmision", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.dOCFechaEmision = :dOCFechaEmision"),
    @NamedQuery(name = "TransaccionComprobantePago.findByDOCImporte", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.dOCImporte = :dOCImporte"),
    @NamedQuery(name = "TransaccionComprobantePago.findByDOCMoneda", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.dOCMoneda = :dOCMoneda"),
    @NamedQuery(name = "TransaccionComprobantePago.findByPagoFecha", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.pagoFecha = :pagoFecha"),
    @NamedQuery(name = "TransaccionComprobantePago.findByPagoNumero", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.pagoNumero = :pagoNumero"),
    @NamedQuery(name = "TransaccionComprobantePago.findByPagoImporteSR", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.pagoImporteSR = :pagoImporteSR"),
    @NamedQuery(name = "TransaccionComprobantePago.findByPagoMoneda", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.pagoMoneda = :pagoMoneda"),
    @NamedQuery(name = "TransaccionComprobantePago.findByCPImporte", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.cPImporte = :cPImporte"),
    @NamedQuery(name = "TransaccionComprobantePago.findByCPMoneda", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.cPMoneda = :cPMoneda"),
    @NamedQuery(name = "TransaccionComprobantePago.findByCPFecha", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.cPFecha = :cPFecha"),
    @NamedQuery(name = "TransaccionComprobantePago.findByCPImporteTotal", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.cPImporteTotal = :cPImporteTotal"),
    @NamedQuery(name = "TransaccionComprobantePago.findByCPMonedaMontoNeto", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.cPMonedaMontoNeto = :cPMonedaMontoNeto"),
    @NamedQuery(name = "TransaccionComprobantePago.findByTCMonedaRef", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.tCMonedaRef = :tCMonedaRef"),
    @NamedQuery(name = "TransaccionComprobantePago.findByTCMonedaObj", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.tCMonedaObj = :tCMonedaObj"),
    @NamedQuery(name = "TransaccionComprobantePago.findByTCFactor", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.tCFactor = :tCFactor"),
    @NamedQuery(name = "TransaccionComprobantePago.findByTCFecha", query = "SELECT t FROM TransaccionComprobantePago t WHERE t.tCFecha = :tCFecha")})
public class TransaccionComprobantePago implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionComprobantePagoPK transaccionComprobantePagoPK;
    @Column(name = "DOC_Tipo")
    private String dOCTipo;
    @Column(name = "DOC_Numero")
    private String dOCNumero;
    @Column(name = "DOC_FechaEmision")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dOCFechaEmision;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DOC_Importe")
    private BigDecimal dOCImporte;
    @Column(name = "DOC_Moneda")
    private String dOCMoneda;
    @Column(name = "PagoFecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pagoFecha;
    @Column(name = "PagoNumero")
    private String pagoNumero;
    @Column(name = "PagoImporteSR")
    private BigDecimal pagoImporteSR;
    @Column(name = "PagoMoneda")
    private String pagoMoneda;
    @Column(name = "CP_Importe")
    private BigDecimal cPImporte;
    @Column(name = "CP_Moneda")
    private String cPMoneda;
    @Column(name = "CP_Fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cPFecha;
    @Column(name = "CP_ImporteTotal")
    private BigDecimal cPImporteTotal;
    @Column(name = "CP_MonedaMontoNeto")
    private String cPMonedaMontoNeto;
    @Column(name = "TC_MonedaRef")
    private String tCMonedaRef;
    @Column(name = "TC_MonedaObj")
    private String tCMonedaObj;
    @Column(name = "TC_Factor")
    private BigDecimal tCFactor;
    @Column(name = "TC_Fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tCFecha;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionComprobantePago() {
    }

    public TransaccionComprobantePago(TransaccionComprobantePagoPK transaccionComprobantePagoPK) {
        this.transaccionComprobantePagoPK = transaccionComprobantePagoPK;
    }

    public TransaccionComprobantePago(String fEId, int nroOrden) {
        this.transaccionComprobantePagoPK = new TransaccionComprobantePagoPK(fEId, nroOrden);
    }

    public TransaccionComprobantePagoPK getTransaccionComprobantePagoPK() {
        return transaccionComprobantePagoPK;
    }

    public void setTransaccionComprobantePagoPK(TransaccionComprobantePagoPK transaccionComprobantePagoPK) {
        this.transaccionComprobantePagoPK = transaccionComprobantePagoPK;
    }

    public String getDOCTipo() {
        return dOCTipo;
    }

    public void setDOCTipo(String dOCTipo) {
        this.dOCTipo = dOCTipo;
    }

    public String getDOCNumero() {
        return dOCNumero;
    }

    public void setDOCNumero(String dOCNumero) {
        this.dOCNumero = dOCNumero;
    }

    public Date getDOCFechaEmision() {
        return dOCFechaEmision;
    }

    public void setDOCFechaEmision(Date dOCFechaEmision) {
        this.dOCFechaEmision = dOCFechaEmision;
    }

    public BigDecimal getDOCImporte() {
        return dOCImporte;
    }

    public void setDOCImporte(BigDecimal dOCImporte) {
        this.dOCImporte = dOCImporte;
    }

    public String getDOCMoneda() {
        return dOCMoneda;
    }

    public void setDOCMoneda(String dOCMoneda) {
        this.dOCMoneda = dOCMoneda;
    }

    public Date getPagoFecha() {
        return pagoFecha;
    }

    public void setPagoFecha(Date pagoFecha) {
        this.pagoFecha = pagoFecha;
    }

    public String getPagoNumero() {
        return pagoNumero;
    }

    public void setPagoNumero(String pagoNumero) {
        this.pagoNumero = pagoNumero;
    }

    public BigDecimal getPagoImporteSR() {
        return pagoImporteSR;
    }

    public void setPagoImporteSR(BigDecimal pagoImporteSR) {
        this.pagoImporteSR = pagoImporteSR;
    }

    public String getPagoMoneda() {
        return pagoMoneda;
    }

    public void setPagoMoneda(String pagoMoneda) {
        this.pagoMoneda = pagoMoneda;
    }

    public BigDecimal getCPImporte() {
        return cPImporte;
    }

    public void setCPImporte(BigDecimal cPImporte) {
        this.cPImporte = cPImporte;
    }

    public String getCPMoneda() {
        return cPMoneda;
    }

    public void setCPMoneda(String cPMoneda) {
        this.cPMoneda = cPMoneda;
    }

    public Date getCPFecha() {
        return cPFecha;
    }

    public void setCPFecha(Date cPFecha) {
        this.cPFecha = cPFecha;
    }

    public BigDecimal getCPImporteTotal() {
        return cPImporteTotal;
    }

    public void setCPImporteTotal(BigDecimal cPImporteTotal) {
        this.cPImporteTotal = cPImporteTotal;
    }

    public String getCPMonedaMontoNeto() {
        return cPMonedaMontoNeto;
    }

    public void setCPMonedaMontoNeto(String cPMonedaMontoNeto) {
        this.cPMonedaMontoNeto = cPMonedaMontoNeto;
    }

    public String getTCMonedaRef() {
        return tCMonedaRef;
    }

    public void setTCMonedaRef(String tCMonedaRef) {
        this.tCMonedaRef = tCMonedaRef;
    }

    public String getTCMonedaObj() {
        return tCMonedaObj;
    }

    public void setTCMonedaObj(String tCMonedaObj) {
        this.tCMonedaObj = tCMonedaObj;
    }

    public BigDecimal getTCFactor() {
        return tCFactor;
    }

    public void setTCFactor(BigDecimal tCFactor) {
        this.tCFactor = tCFactor;
    }

    public Date getTCFecha() {
        return tCFecha;
    }

    public void setTCFecha(Date tCFecha) {
        this.tCFecha = tCFecha;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionComprobantePagoPK != null ? transaccionComprobantePagoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionComprobantePago)) {
            return false;
        }
        TransaccionComprobantePago other = (TransaccionComprobantePago) object;
        if ((this.transaccionComprobantePagoPK == null && other.transaccionComprobantePagoPK != null) || (this.transaccionComprobantePagoPK != null && !this.transaccionComprobantePagoPK.equals(other.transaccionComprobantePagoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionComprobantePago[ transaccionComprobantePagoPK=" + transaccionComprobantePagoPK + " ]";
    }
    
}
