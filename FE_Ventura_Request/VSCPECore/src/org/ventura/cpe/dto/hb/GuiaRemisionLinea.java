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
@Table(name = "GUIA_REMISION_LINEA")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "GuiaRemisionLinea.findAll", query = "SELECT g FROM GuiaRemisionLinea g"),
        @NamedQuery(name = "GuiaRemisionLinea.findById", query = "SELECT g FROM GuiaRemisionLinea g WHERE g.guiaRemisionLineaPK.id = :id"),
        @NamedQuery(name = "GuiaRemisionLinea.findByIDLinea", query = "SELECT g FROM GuiaRemisionLinea g WHERE g.guiaRemisionLineaPK.iDLinea = :iDLinea"),
        @NamedQuery(name = "GuiaRemisionLinea.findByBTNumOrden", query = "SELECT g FROM GuiaRemisionLinea g WHERE g.bTNumOrden = :bTNumOrden"),
        @NamedQuery(name = "GuiaRemisionLinea.findByBTCantidad", query = "SELECT g FROM GuiaRemisionLinea g WHERE g.bTCantidad = :bTCantidad"),
        @NamedQuery(name = "GuiaRemisionLinea.findByBTUnidMedida", query = "SELECT g FROM GuiaRemisionLinea g WHERE g.bTUnidMedida = :bTUnidMedida"),
        @NamedQuery(name = "GuiaRemisionLinea.findByBTDescripcion", query = "SELECT g FROM GuiaRemisionLinea g WHERE g.bTDescripcion = :bTDescripcion"),
        @NamedQuery(name = "GuiaRemisionLinea.findByBTCodigo", query = "SELECT g FROM GuiaRemisionLinea g WHERE g.bTCodigo = :bTCodigo")})
public class GuiaRemisionLinea implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected GuiaRemisionLineaPK guiaRemisionLineaPK;

    @Column(name = "BT_NumOrden")
    private Integer bTNumOrden;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "BT_Cantidad")
    private BigDecimal bTCantidad;

    @Column(name = "BT_UnidMedida")
    private String bTUnidMedida;

    @Column(name = "BT_Descripcion")
    private String bTDescripcion;

    @Column(name = "BT_Codigo")
    private String bTCodigo;

    @JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private GuiaRemision guiaRemision;

    public GuiaRemisionLinea() {
    }

    public GuiaRemisionLinea(GuiaRemisionLineaPK guiaRemisionLineaPK) {
        this.guiaRemisionLineaPK = guiaRemisionLineaPK;
    }

    public GuiaRemisionLinea(String id, int iDLinea) {
        this.guiaRemisionLineaPK = new GuiaRemisionLineaPK(id, iDLinea);
    }

    public GuiaRemisionLineaPK getGuiaRemisionLineaPK() {
        return guiaRemisionLineaPK;
    }

    public void setGuiaRemisionLineaPK(GuiaRemisionLineaPK guiaRemisionLineaPK) {
        this.guiaRemisionLineaPK = guiaRemisionLineaPK;
    }

    public Integer getBTNumOrden() {
        return bTNumOrden;
    }

    public void setBTNumOrden(Integer bTNumOrden) {
        this.bTNumOrden = bTNumOrden;
    }

    public BigDecimal getBTCantidad() {
        return bTCantidad;
    }

    public void setBTCantidad(BigDecimal bTCantidad) {
        this.bTCantidad = bTCantidad;
    }

    public String getBTUnidMedida() {
        return bTUnidMedida;
    }

    public void setBTUnidMedida(String bTUnidMedida) {
        this.bTUnidMedida = bTUnidMedida;
    }

    public String getBTDescripcion() {
        return bTDescripcion;
    }

    public void setBTDescripcion(String bTDescripcion) {
        this.bTDescripcion = bTDescripcion;
    }

    public String getBTCodigo() {
        return bTCodigo;
    }

    public void setBTCodigo(String bTCodigo) {
        this.bTCodigo = bTCodigo;
    }

    public GuiaRemision getGuiaRemision() {
        return guiaRemision;
    }

    public void setGuiaRemision(GuiaRemision guiaRemision) {
        this.guiaRemision = guiaRemision;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (guiaRemisionLineaPK != null ? guiaRemisionLineaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GuiaRemisionLinea)) {
            return false;
        }
        GuiaRemisionLinea other = (GuiaRemisionLinea) object;
        if ((this.guiaRemisionLineaPK == null && other.guiaRemisionLineaPK != null) || (this.guiaRemisionLineaPK != null && !this.guiaRemisionLineaPK.equals(other.guiaRemisionLineaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.GuiaRemisionLinea[ guiaRemisionLineaPK=" + guiaRemisionLineaPK + " ]";
    }

}
