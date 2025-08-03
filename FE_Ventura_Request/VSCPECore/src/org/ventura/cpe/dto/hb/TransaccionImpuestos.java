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
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_IMPUESTOS")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TransaccionImpuestos.findAll", query = "SELECT t FROM TransaccionImpuestos t"),
        @NamedQuery(name = "TransaccionImpuestos.findByFEId", query = "SELECT t FROM TransaccionImpuestos t WHERE t.transaccionImpuestosPK.fEId = :fEId"),
        @NamedQuery(name = "TransaccionImpuestos.findByLineId", query = "SELECT t FROM TransaccionImpuestos t WHERE t.transaccionImpuestosPK.lineId = :lineId"),
        @NamedQuery(name = "TransaccionImpuestos.findByMoneda", query = "SELECT t FROM TransaccionImpuestos t WHERE t.moneda = :moneda"),
        @NamedQuery(name = "TransaccionImpuestos.findByMonto", query = "SELECT t FROM TransaccionImpuestos t WHERE t.monto = :monto"),
        @NamedQuery(name = "TransaccionImpuestos.findByPorcentaje", query = "SELECT t FROM TransaccionImpuestos t WHERE t.porcentaje = :porcentaje"),
        @NamedQuery(name = "TransaccionImpuestos.findByTipoTributo", query = "SELECT t FROM TransaccionImpuestos t WHERE t.tipoTributo = :tipoTributo"),
        @NamedQuery(name = "TransaccionImpuestos.findByTipoAfectacion", query = "SELECT t FROM TransaccionImpuestos t WHERE t.tipoAfectacion = :tipoAfectacion"),
        @NamedQuery(name = "TransaccionLineaImpuestos.findByTierRange", query = "SELECT t FROM TransaccionLineaImpuestos t WHERE t.tierRange = :tierRange")})
public class TransaccionImpuestos implements Serializable {

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "ValorVenta")
    private BigDecimal valorVenta;

    @Column(name = "Abreviatura")
    private String abreviatura;

    @Column(name = "Codigo")
    private String codigo;

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TransaccionImpuestosPK transaccionImpuestosPK;

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

    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;


    public TransaccionImpuestos() {
    }

    public TransaccionImpuestos(TransaccionImpuestosPK transaccionImpuestosPK) {
        this.transaccionImpuestosPK = transaccionImpuestosPK;
    }

    public TransaccionImpuestos(String fEId, int lineId) {
        this.transaccionImpuestosPK = new TransaccionImpuestosPK(fEId, lineId);
    }

    public TransaccionImpuestosPK getTransaccionImpuestosPK() {
        return transaccionImpuestosPK;
    }

    public void setTransaccionImpuestosPK(TransaccionImpuestosPK transaccionImpuestosPK) {
        this.transaccionImpuestosPK = transaccionImpuestosPK;
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

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public String getTierRange() {
        return tierRange;
    }

    public void setTierRange(String tierRange) {
        this.tierRange = tierRange;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionImpuestosPK != null ? transaccionImpuestosPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionImpuestos)) {
            return false;
        }
        TransaccionImpuestos other = (TransaccionImpuestos) object;
        if ((this.transaccionImpuestosPK == null && other.transaccionImpuestosPK != null) || (this.transaccionImpuestosPK != null && !this.transaccionImpuestosPK.equals(other.transaccionImpuestosPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionImpuestos[ transaccionImpuestosPK=" + transaccionImpuestosPK + " ]";
    }

    public BigDecimal getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(BigDecimal valorVenta) {
        this.valorVenta = valorVenta;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
