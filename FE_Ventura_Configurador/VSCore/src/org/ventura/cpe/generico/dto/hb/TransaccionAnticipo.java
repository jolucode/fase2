/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generico.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_ANTICIPO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionAnticipo.findAll", query = "SELECT t FROM TransaccionAnticipo t"),
    @NamedQuery(name = "TransaccionAnticipo.findByFEId", query = "SELECT t FROM TransaccionAnticipo t WHERE t.transaccionAnticipoPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionAnticipo.findByNroAnticipo", query = "SELECT t FROM TransaccionAnticipo t WHERE t.transaccionAnticipoPK.nroAnticipo = :nroAnticipo"),
    @NamedQuery(name = "TransaccionAnticipo.findByAnticipoMonto", query = "SELECT t FROM TransaccionAnticipo t WHERE t.anticipoMonto = :anticipoMonto"),
    @NamedQuery(name = "TransaccionAnticipo.findByAntiDOCTipo", query = "SELECT t FROM TransaccionAnticipo t WHERE t.antiDOCTipo = :antiDOCTipo"),
    @NamedQuery(name = "TransaccionAnticipo.findByAntiDOCSerieCorrelativo", query = "SELECT t FROM TransaccionAnticipo t WHERE t.antiDOCSerieCorrelativo = :antiDOCSerieCorrelativo"),
    @NamedQuery(name = "TransaccionAnticipo.findByDOCNumero", query = "SELECT t FROM TransaccionAnticipo t WHERE t.dOCNumero = :dOCNumero"),
    @NamedQuery(name = "TransaccionAnticipo.findByDOCMoneda", query = "SELECT t FROM TransaccionAnticipo t WHERE t.dOCMoneda = :dOCMoneda"),
    @NamedQuery(name = "TransaccionAnticipo.findByDOCTipo", query = "SELECT t FROM TransaccionAnticipo t WHERE t.dOCTipo = :dOCTipo")})
public class TransaccionAnticipo implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionAnticipoPK transaccionAnticipoPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Anticipo_Monto")
    private BigDecimal anticipoMonto;
    @Column(name = "AntiDOC_Tipo")
    private String antiDOCTipo;
    @Column(name = "AntiDOC_Serie_Correlativo")
    private String antiDOCSerieCorrelativo;
    @Column(name = "DOC_Numero")
    private String dOCNumero;
    @Column(name = "DOC_Moneda")
    private String dOCMoneda;
    @Column(name = "DOC_Tipo")
    private String dOCTipo;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionAnticipo() {
    }

    public TransaccionAnticipo(TransaccionAnticipoPK transaccionAnticipoPK) {
        this.transaccionAnticipoPK = transaccionAnticipoPK;
    }

    public TransaccionAnticipo(String fEId, int nroAnticipo) {
        this.transaccionAnticipoPK = new TransaccionAnticipoPK(fEId, nroAnticipo);
    }

    public TransaccionAnticipoPK getTransaccionAnticipoPK() {
        return transaccionAnticipoPK;
    }

    public void setTransaccionAnticipoPK(TransaccionAnticipoPK transaccionAnticipoPK) {
        this.transaccionAnticipoPK = transaccionAnticipoPK;
    }

    public BigDecimal getAnticipoMonto() {
        return anticipoMonto;
    }

    public void setAnticipoMonto(BigDecimal anticipoMonto) {
        this.anticipoMonto = anticipoMonto;
    }

    public String getAntiDOCTipo() {
        return antiDOCTipo;
    }

    public void setAntiDOCTipo(String antiDOCTipo) {
        this.antiDOCTipo = antiDOCTipo;
    }

    public String getAntiDOCSerieCorrelativo() {
        return antiDOCSerieCorrelativo;
    }

    public void setAntiDOCSerieCorrelativo(String antiDOCSerieCorrelativo) {
        this.antiDOCSerieCorrelativo = antiDOCSerieCorrelativo;
    }

    public String getDOCNumero() {
        return dOCNumero;
    }

    public void setDOCNumero(String dOCNumero) {
        this.dOCNumero = dOCNumero;
    }

    public String getDOCMoneda() {
        return dOCMoneda;
    }

    public void setDOCMoneda(String dOCMoneda) {
        this.dOCMoneda = dOCMoneda;
    }

    public String getDOCTipo() {
        return dOCTipo;
    }

    public void setDOCTipo(String dOCTipo) {
        this.dOCTipo = dOCTipo;
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
        hash += (transaccionAnticipoPK != null ? transaccionAnticipoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionAnticipo)) {
            return false;
        }
        TransaccionAnticipo other = (TransaccionAnticipo) object;
        if ((this.transaccionAnticipoPK == null && other.transaccionAnticipoPK != null) || (this.transaccionAnticipoPK != null && !this.transaccionAnticipoPK.equals(other.transaccionAnticipoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.generico.dto.hb.TransaccionAnticipo[ transaccionAnticipoPK=" + transaccionAnticipoPK + " ]";
    }
    
}
