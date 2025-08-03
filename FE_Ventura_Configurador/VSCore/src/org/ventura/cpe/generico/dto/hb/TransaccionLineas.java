/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generico.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_LINEAS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionLineas.findAll", query = "SELECT t FROM TransaccionLineas t"),
    @NamedQuery(name = "TransaccionLineas.findByFEId", query = "SELECT t FROM TransaccionLineas t WHERE t.transaccionLineasPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionLineas.findByNroOrden", query = "SELECT t FROM TransaccionLineas t WHERE t.transaccionLineasPK.nroOrden = :nroOrden"),
    @NamedQuery(name = "TransaccionLineas.findByDSCTOPorcentaje", query = "SELECT t FROM TransaccionLineas t WHERE t.dSCTOPorcentaje = :dSCTOPorcentaje"),
    @NamedQuery(name = "TransaccionLineas.findByDSCTOMonto", query = "SELECT t FROM TransaccionLineas t WHERE t.dSCTOMonto = :dSCTOMonto"),
    @NamedQuery(name = "TransaccionLineas.findByDescripcion", query = "SELECT t FROM TransaccionLineas t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "TransaccionLineas.findByPrecioDscto", query = "SELECT t FROM TransaccionLineas t WHERE t.precioDscto = :precioDscto"),
    @NamedQuery(name = "TransaccionLineas.findByPrecioIGV", query = "SELECT t FROM TransaccionLineas t WHERE t.precioIGV = :precioIGV"),
    @NamedQuery(name = "TransaccionLineas.findByTotalLineaSinIGV", query = "SELECT t FROM TransaccionLineas t WHERE t.totalLineaSinIGV = :totalLineaSinIGV"),
    @NamedQuery(name = "TransaccionLineas.findByTotalLineaConIGV", query = "SELECT t FROM TransaccionLineas t WHERE t.totalLineaConIGV = :totalLineaConIGV"),
    @NamedQuery(name = "TransaccionLineas.findByPrecioRefMonto", query = "SELECT t FROM TransaccionLineas t WHERE t.precioRefMonto = :precioRefMonto"),
    @NamedQuery(name = "TransaccionLineas.findByPrecioRefCodigo", query = "SELECT t FROM TransaccionLineas t WHERE t.precioRefCodigo = :precioRefCodigo"),
    @NamedQuery(name = "TransaccionLineas.findByCantidad", query = "SELECT t FROM TransaccionLineas t WHERE t.cantidad = :cantidad"),
    @NamedQuery(name = "TransaccionLineas.findByUnidad", query = "SELECT t FROM TransaccionLineas t WHERE t.unidad = :unidad"),
    @NamedQuery(name = "TransaccionLineas.findByUnidadSunat", query = "SELECT t FROM TransaccionLineas t WHERE t.unidadSunat = :unidadSunat"),
    @NamedQuery(name = "TransaccionLineas.findByCodArticulo", query = "SELECT t FROM TransaccionLineas t WHERE t.codArticulo = :codArticulo")})
public class TransaccionLineas implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionLineasPK transaccionLineasPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DSCTO_Porcentaje")
    private BigDecimal dSCTOPorcentaje;
    @Column(name = "DSCTO_Monto")
    private BigDecimal dSCTOMonto;
    @Column(name = "Descripcion")
    private String descripcion;
    @Column(name = "PrecioDscto")
    private BigDecimal precioDscto;
    @Column(name = "PrecioIGV")
    private BigDecimal precioIGV;
    @Column(name = "TotalLineaSinIGV")
    private BigDecimal totalLineaSinIGV;
    @Column(name = "TotalLineaConIGV")
    private BigDecimal totalLineaConIGV;
    @Column(name = "PrecioRef_Monto")
    private BigDecimal precioRefMonto;
    @Column(name = "PrecioRef_Codigo")
    private String precioRefCodigo;
    @Column(name = "Cantidad")
    private BigDecimal cantidad;
    @Column(name = "Unidad")
    private String unidad;
    @Column(name = "UnidadSunat")
    private String unidadSunat;
    @Column(name = "CodArticulo")
    private String codArticulo;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionLineas")
    private List<TransaccionLineaImpuestos> transaccionLineaImpuestosList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionLineas")
    private List<TransaccionLineasCampousuario> transaccionLineasCampousuarioList;
    

    public TransaccionLineas() {
    }

    public TransaccionLineas(TransaccionLineasPK transaccionLineasPK) {
        this.transaccionLineasPK = transaccionLineasPK;
    }

    public TransaccionLineas(String fEId, int nroOrden) {
        this.transaccionLineasPK = new TransaccionLineasPK(fEId, nroOrden);
    }

    public TransaccionLineasPK getTransaccionLineasPK() {
        return transaccionLineasPK;
    }

    public void setTransaccionLineasPK(TransaccionLineasPK transaccionLineasPK) {
        this.transaccionLineasPK = transaccionLineasPK;
    }

    public BigDecimal getDSCTOPorcentaje() {
        return dSCTOPorcentaje;
    }

    public void setDSCTOPorcentaje(BigDecimal dSCTOPorcentaje) {
        this.dSCTOPorcentaje = dSCTOPorcentaje;
    }

    public BigDecimal getDSCTOMonto() {
        return dSCTOMonto;
    }

    public void setDSCTOMonto(BigDecimal dSCTOMonto) {
        this.dSCTOMonto = dSCTOMonto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioDscto() {
        return precioDscto;
    }

    public void setPrecioDscto(BigDecimal precioDscto) {
        this.precioDscto = precioDscto;
    }

    public BigDecimal getPrecioIGV() {
        return precioIGV;
    }

    public void setPrecioIGV(BigDecimal precioIGV) {
        this.precioIGV = precioIGV;
    }

    public BigDecimal getTotalLineaSinIGV() {
        return totalLineaSinIGV;
    }

    public void setTotalLineaSinIGV(BigDecimal totalLineaSinIGV) {
        this.totalLineaSinIGV = totalLineaSinIGV;
    }

    public BigDecimal getTotalLineaConIGV() {
        return totalLineaConIGV;
    }

    public void setTotalLineaConIGV(BigDecimal totalLineaConIGV) {
        this.totalLineaConIGV = totalLineaConIGV;
    }

    public BigDecimal getPrecioRefMonto() {
        return precioRefMonto;
    }

    public void setPrecioRefMonto(BigDecimal precioRefMonto) {
        this.precioRefMonto = precioRefMonto;
    }

    public String getPrecioRefCodigo() {
        return precioRefCodigo;
    }

    public void setPrecioRefCodigo(String precioRefCodigo) {
        this.precioRefCodigo = precioRefCodigo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getUnidadSunat() {
        return unidadSunat;
    }

    public void setUnidadSunat(String unidadSunat) {
        this.unidadSunat = unidadSunat;
    }

    public String getCodArticulo() {
        return codArticulo;
    }

    public void setCodArticulo(String codArticulo) {
        this.codArticulo = codArticulo;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    @XmlTransient
    public List<TransaccionLineaImpuestos> getTransaccionLineaImpuestosList() {
        return transaccionLineaImpuestosList;
    }

    public void setTransaccionLineaImpuestosList(List<TransaccionLineaImpuestos> transaccionLineaImpuestosList) {
        this.transaccionLineaImpuestosList = transaccionLineaImpuestosList;
    }

    @XmlTransient
    public List<TransaccionLineasCampousuario> getTransaccionLineasCampousuarioList() {
        return transaccionLineasCampousuarioList;
    }

    public void setTransaccionLineasCampousuarioList(List<TransaccionLineasCampousuario> transaccionLineasCampousuarioList) {
        this.transaccionLineasCampousuarioList = transaccionLineasCampousuarioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionLineasPK != null ? transaccionLineasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionLineas)) {
            return false;
        }
        TransaccionLineas other = (TransaccionLineas) object;
        if ((this.transaccionLineasPK == null && other.transaccionLineasPK != null) || (this.transaccionLineasPK != null && !this.transaccionLineasPK.equals(other.transaccionLineasPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.generico.dto.hb.TransaccionLineas[ transaccionLineasPK=" + transaccionLineasPK + " ]";
    }
    
}
