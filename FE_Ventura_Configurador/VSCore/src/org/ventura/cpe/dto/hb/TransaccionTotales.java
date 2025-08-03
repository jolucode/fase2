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
 * @author Percy
 */
@Entity
@Table(name = "TRANSACCION_TOTALES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionTotales.findAll", query = "SELECT t FROM TransaccionTotales t"),
    @NamedQuery(name = "TransaccionTotales.findByFEId", query = "SELECT t FROM TransaccionTotales t WHERE t.transaccionTotalesPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionTotales.findById", query = "SELECT t FROM TransaccionTotales t WHERE t.transaccionTotalesPK.id = :id"),
    @NamedQuery(name = "TransaccionTotales.findByMonto", query = "SELECT t FROM TransaccionTotales t WHERE t.monto = :monto"),
    @NamedQuery(name = "TransaccionTotales.findByPrcnt", query = "SELECT t FROM TransaccionTotales t WHERE t.prcnt = :prcnt")})
public class TransaccionTotales implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionTotalesPK transaccionTotalesPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Monto")
    private BigDecimal monto;
    @Column(name = "Prcnt")
    private BigDecimal prcnt;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionTotales() {
    }

    public TransaccionTotales(TransaccionTotalesPK transaccionTotalesPK) {
        this.transaccionTotalesPK = transaccionTotalesPK;
    }

    public TransaccionTotales(String fEId, String id) {
        this.transaccionTotalesPK = new TransaccionTotalesPK(fEId, id);
    }

    public TransaccionTotalesPK getTransaccionTotalesPK() {
        return transaccionTotalesPK;
    }

    public void setTransaccionTotalesPK(TransaccionTotalesPK transaccionTotalesPK) {
        this.transaccionTotalesPK = transaccionTotalesPK;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getPrcnt() {
        return prcnt;
    }

    public void setPrcnt(BigDecimal prcnt) {
        this.prcnt = prcnt;
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
        hash += (transaccionTotalesPK != null ? transaccionTotalesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionTotales)) {
            return false;
        }
        TransaccionTotales other = (TransaccionTotales) object;
        if ((this.transaccionTotalesPK == null && other.transaccionTotalesPK != null) || (this.transaccionTotalesPK != null && !this.transaccionTotalesPK.equals(other.transaccionTotalesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionTotales[ transaccionTotalesPK=" + transaccionTotalesPK + " ]";
    }
    
}
