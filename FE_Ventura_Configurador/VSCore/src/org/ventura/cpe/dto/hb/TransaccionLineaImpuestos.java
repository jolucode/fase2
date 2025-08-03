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
@Table(name = "TRANSACCION_LINEA_IMPUESTOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionLineaImpuestos.findAll", query = "SELECT t FROM TransaccionLineaImpuestos t"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByFEId", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.transaccionLineaImpuestosPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByNroOrden", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.transaccionLineaImpuestosPK.nroOrden = :nroOrden"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByMoneda", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.moneda = :moneda"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByMonto", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.monto = :monto"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByPorcentaje", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.porcentaje = :porcentaje"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByTipoTributo", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.tipoTributo = :tipoTributo"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByTipoAfectacion", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.tipoAfectacion = :tipoAfectacion"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByTierRange", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.tierRange = :tierRange"),
    @NamedQuery(name = "TransaccionLineaImpuestos.findByLineId", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.transaccionLineaImpuestosPK.lineId = :lineId")})
public class TransaccionLineaImpuestos implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionLineaImpuestosPK transaccionLineaImpuestosPK;
    @Column(name = "Moneda")
    private String moneda;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Monto")
    private BigDecimal monto;
    @Column(name = "Porcentaje")
    private BigDecimal porcentaje;
    @Column(name = "TipoTributo")
    private String tipoTributo;
    @Column(name = "TipoAfectacion")
    private String tipoAfectacion;
    @Column(name = "TierRange")
    private String tierRange;
    @JoinColumns({
        @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false),
        @JoinColumn(name = "NroOrden", referencedColumnName = "NroOrden", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TransaccionLineas transaccionLineas;

    public TransaccionLineaImpuestos() {
    }

    public TransaccionLineaImpuestos(TransaccionLineaImpuestosPK transaccionLineaImpuestosPK) {
        this.transaccionLineaImpuestosPK = transaccionLineaImpuestosPK;
    }

    public TransaccionLineaImpuestos(String fEId, int nroOrden, int lineId) {
        this.transaccionLineaImpuestosPK = new TransaccionLineaImpuestosPK(fEId, nroOrden, lineId);
    }

    public TransaccionLineaImpuestosPK getTransaccionLineaImpuestosPK() {
        return transaccionLineaImpuestosPK;
    }

    public void setTransaccionLineaImpuestosPK(TransaccionLineaImpuestosPK transaccionLineaImpuestosPK) {
        this.transaccionLineaImpuestosPK = transaccionLineaImpuestosPK;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(String tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public String getTipoAfectacion() {
        return tipoAfectacion;
    }

    public void setTipoAfectacion(String tipoAfectacion) {
        this.tipoAfectacion = tipoAfectacion;
    }

    public String getTierRange() {
        return tierRange;
    }

    public void setTierRange(String tierRange) {
        this.tierRange = tierRange;
    }

    public TransaccionLineas getTransaccionLineas() {
        return transaccionLineas;
    }

    public void setTransaccionLineas(TransaccionLineas transaccionLineas) {
        this.transaccionLineas = transaccionLineas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionLineaImpuestosPK != null ? transaccionLineaImpuestosPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionLineaImpuestos)) {
            return false;
        }
        TransaccionLineaImpuestos other = (TransaccionLineaImpuestos) object;
        if ((this.transaccionLineaImpuestosPK == null && other.transaccionLineaImpuestosPK != null) || (this.transaccionLineaImpuestosPK != null && !this.transaccionLineaImpuestosPK.equals(other.transaccionLineaImpuestosPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionLineaImpuestos[ transaccionLineaImpuestosPK=" + transaccionLineaImpuestosPK + " ]";
    }
    
}
